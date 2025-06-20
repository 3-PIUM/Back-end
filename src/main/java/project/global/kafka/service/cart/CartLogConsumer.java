package project.global.kafka.service.cart;

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
import project.global.kafka.dto.cart.CartEventDTO;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CartLogConsumer {

    private static final Logger CART_EVENT_LOGGER = LoggerFactory.getLogger("CART_EVENT");
    private static final Logger CART_ERROR_LOGGER = LoggerFactory.getLogger("CART_ERROR");

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(
            topics = "cart-events",
            groupId = "cart-group-id",
            concurrency = "5"
    )
    public void consumeCartEvent(
            @Payload String eventData,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        try {
            log.debug("장바구니 이벤트 수신: topic={}, partition={}, offset={}, key={}",
                    topic, partition, offset, key);

            // JSON -> DTO 변환
            CartEventDTO cartEventDTO = objectMapper.readValue(eventData, CartEventDTO.class);

            // logstash에서 파싱하기 좋은 형태로 변환
            String structuredCartLog = createStructuredCartLog(cartEventDTO, topic, partition, offset, key);
            CART_EVENT_LOGGER.info(structuredCartLog);

            log.debug("장바구니 로그 파일 저장 완료: userId={}, topic={}, partition={}, offset={}",
                    cartEventDTO.getMemberId(), topic, partition, offset);
        } catch (Exception e) {
            log.error("구매 이벤트 처리 실패: topic={}, partition={}, offset={}, key={}",
                    topic, partition, offset, key, e);

            // 처리 실패한 메시지 별도 처리
            handleConsumerError(eventData, topic, partition, offset, key, e);
        }
    }

    private String createStructuredCartLog(CartEventDTO dto, String topic,
                                           int partition, long offset, String key) {
        try {
            // Logstash에서 파싱하기 쉬운 JSON 형태로 구조화
            Map<String, Object> logData = new HashMap<>();
            logData.put("@timestamp", java.time.Instant.now().toString());
            logData.put("event_date", dto.getEventTime());
            logData.put("event_type", "cart");
            logData.put("member_id", dto.getMemberId());
            logData.put("birth", dto.getBirth());
            logData.put("gender", dto.getGender());
            logData.put("area", dto.getArea());
            logData.put("skin_type", dto.getSkinType());
            logData.put("skin_issues", dto.getSkinIssues());
            logData.put("personal_type", dto.getPersonalType());
            logData.put("item_id", dto.getItemId());
            logData.put("kafka_topic", topic);
            logData.put("kafka_partition", partition);
            logData.put("kafka_offset", offset);
            logData.put("kafka_key", key);

            return objectMapper.writeValueAsString(logData);
        } catch (Exception e) {
            log.error("구조화된 로그 생성 실패", e);
            return String.format("PARSE_ERROR: %s", dto.toString());
        }
    }

    private void handleConsumerError(String eventData, String topic, int partition,
                                     long offset, String key, Exception e) {
        try {
            Map<String, Object> errorLog = new HashMap<>();
            errorLog.put("@timestamp", java.time.Instant.now().toString());
            errorLog.put("event_type", "cart_error");
            errorLog.put("error_message", e.getMessage());
            errorLog.put("error_class", e.getClass().getSimpleName());
            errorLog.put("raw_event_data", eventData);
            errorLog.put("kafka_topic", topic);
            errorLog.put("kafka_partition", partition);
            errorLog.put("kafka_offset", offset);
            errorLog.put("kafka_key", key);

            CART_ERROR_LOGGER.error(objectMapper.writeValueAsString(errorLog));
        } catch (Exception ex) {
            log.error("에러 로그 저장 실패: topic={}, partition={}, offset={}",
                    topic, partition, offset, ex);
        }
    }
}
