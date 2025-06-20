package project.global.kafka.service.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import project.global.kafka.dto.view.ViewEventDTO;


// Consumer - 메시지 소비 및 ElasticSearch 저장
@Component
@Slf4j
@RequiredArgsConstructor
public class ViewLogConsumer {

    // 조회 이벤트 전용 로거
    private static final Logger VIEW_EVENT_LOGGER = LoggerFactory.getLogger("VIEW_EVENT");
    private static final Logger VIEW_ERROR_LOGGER = LoggerFactory.getLogger("VIEW_ERROR");


    private final ObjectMapper objectMapper;

    private final ViewLogService viewLogService;

    @KafkaListener(
            topics = "view-events",
            groupId = "view-log-group",
            concurrency = "10"
    )
    public void consumePurchaseEvent(
            @Payload String eventData,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        try {
            log.debug("조회 이벤트 수신: topic={}, partition={}, offset={}, key={}",
                    topic, partition, offset, key);

            // JSON -> DTO 변환
            ViewEventDTO viewEventDTO = objectMapper.readValue(eventData, ViewEventDTO.class);

            // 구조화된 로그를 파일에 기록
            String structuredLog = createStructuredViewLog(viewEventDTO, topic, partition, offset, key);
            VIEW_EVENT_LOGGER.info(structuredLog);

            // ElasticSearch에 로그 저장
            // viewLogService.saveViewLog(viewEventDTO, topic, partition, offset);

            log.debug("조회 로그 파일 저장 완료: userId={}, topic={}, partition={}, offset={}",
                    viewEventDTO.getMemberId(), topic, partition, offset);

        } catch (Exception e) {
            log.error("조회 이벤트 처리 실패: topic={}, partition={}, offset={}, key={}",
                    topic, partition, offset, key, e);

            handleConsumerError(eventData, topic, partition, offset, key, e);
        }
    }

    private String createStructuredViewLog(ViewEventDTO dto, String topic,
                                           int partition, long offset, String key) {
        try {
            var logData = new java.util.HashMap<String, Object>();
            logData.put("@timestamp", java.time.Instant.now().toString());
            logData.put("event_type", "view");
            logData.put("member_id", dto.getMemberId());
            logData.put("item_id", dto.getItemId());
            logData.put("sub_category", dto.getSubCategory());
            logData.put("event_date", dto.getEventTime());
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
            errorLog.put("event_type", "view_error");
            errorLog.put("error_message", e.getMessage());
            errorLog.put("error_class", e.getClass().getSimpleName());
            errorLog.put("raw_event_data", eventData);
            errorLog.put("kafka_topic", topic);
            errorLog.put("kafka_partition", partition);
            errorLog.put("kafka_offset", offset);
            errorLog.put("kafka_key", key);
            errorLog.put("service", "view-service");

            VIEW_ERROR_LOGGER.error(objectMapper.writeValueAsString(errorLog));
        } catch (Exception ex) {
            log.error("에러 로그 저장 실패: topic={}, partition={}, offset={}",
                    topic, partition, offset, ex);
        }
    }
}

