package project.global.kafka.service.purchase;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import project.global.kafka.dto.purchase.PurchaseEventDTO;

import java.util.HashMap;
import java.util.Map;


// Consumer - 메시지 소비 및 ElasticSearch 저장
@Component
@Slf4j
public class PurchaseEventConsumer {

    // 구매 이벤트 전용 로거 (logback.xml에서 별도 설정)
    private static final Logger PURCHASE_EVENT_LOGGER = LoggerFactory.getLogger("PURCHASE_EVENT");
    private static final Logger PURCHASE_ERROR_LOGGER = LoggerFactory.getLogger("PURCHASE_ERROR");

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PurchaseLogService purchaseLogService;

    @KafkaListener(
            topics = "purchase-events",
            groupId = "purchase-log-group",
            concurrency = "3"
    )
    public void consumePurchaseEvent(
            @Payload String eventData,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        try {
            log.debug("구매 이벤트 수신: topic={}, partition={}, offset={}, key={}",
                    topic, partition, offset, key);

            // JSON -> DTO 변환
            PurchaseEventDTO purchaseEventDTO = objectMapper.readValue(eventData, PurchaseEventDTO.class);

            // 구조화된 로그를 파일에 기록 (JSON 형태로 Logstash가 파싱하기 쉽게)
            String structuredLog = createStructuredPurchaseLog(purchaseEventDTO, topic, partition, offset, key);
            PURCHASE_EVENT_LOGGER.info(structuredLog);

            // ElasticSearch에 로그 저장
            // purchaseLogService.savePurchaseLog(purchaseEventDTO, topic, partition, offset);

            log.debug("구매 로그 파일 저장 완료: userId={}, topic={}, partition={}, offset={}",
                    purchaseEventDTO.getMemberId(), topic, partition, offset);

        } catch (Exception e) {
            log.error("구매 이벤트 처리 실패: topic={}, partition={}, offset={}, key={}",
                    topic, partition, offset, key, e);

            // 처리 실패한 메시지 별도 처리
            handleConsumerError(eventData, topic, partition, offset, key, e);
        }
    }

    private String createStructuredPurchaseLog(PurchaseEventDTO dto, String topic,
                                               int partition, long offset, String key) {
        try {
            // Logstash에서 파싱하기 쉬운 JSON 형태로 구조화
            Map<String, Object> logData = new HashMap<>();
            logData.put("@timestamp", java.time.Instant.now().toString());
            logData.put("event_type", "purchase");
            logData.put("member_id", dto.getMemberId());
            logData.put("birth", dto.getBirth());
            logData.put("gender", dto.getGender());
            logData.put("area", dto.getArea());
            logData.put("personal_type", dto.getPersonalType());
            logData.put("cart_item_ids", dto.getCartItemIds());
            logData.put("purchase_item_ids", dto.getPurchaseItemIds());
            logData.put("kafka_topic", topic);
            logData.put("kafka_partition", partition);
            logData.put("kafka_offset", offset);
            logData.put("kafka_key", key);
            logData.put("environment", System.getProperty("spring.profiles.active", "unknown"));

            return objectMapper.writeValueAsString(logData);
        } catch (Exception e) {
            log.error("구조화된 로그 생성 실패", e);
            return String.format("PARSE_ERROR: %s", dto.toString());
        }
    }

    private void handleConsumerError(String eventData, String topic, int partition,
                                     long offset, String key, Exception e) {
        try {
            var errorLog = new java.util.HashMap<String, Object>();
            errorLog.put("@timestamp", java.time.Instant.now().toString());
            errorLog.put("event_type", "purchase_error");
            errorLog.put("error_message", e.getMessage());
            errorLog.put("error_class", e.getClass().getSimpleName());
            errorLog.put("raw_event_data", eventData);
            errorLog.put("kafka_topic", topic);
            errorLog.put("kafka_partition", partition);
            errorLog.put("kafka_offset", offset);
            errorLog.put("kafka_key", key);

            PURCHASE_ERROR_LOGGER.error(objectMapper.writeValueAsString(errorLog));
        } catch (Exception ex) {
            log.error("에러 로그 저장 실패: topic={}, partition={}, offset={}",
                    topic, partition, offset, ex);
        }
    }
}
