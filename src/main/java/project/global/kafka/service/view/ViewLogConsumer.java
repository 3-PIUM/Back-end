package project.global.kafka.service.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            log.info("조회 이벤트 수신: topic={}, partition={}, offset={}, key={}",
                    topic, partition, offset, key);

            // JSON -> DTO 변환
            ViewEventDTO viewEventDTO = objectMapper.readValue(eventData, ViewEventDTO.class);

            // ElasticSearch에 로그 저장
            viewLogService.saveViewLog(viewEventDTO, topic, partition, offset);

            log.info("조회 로그 저장 완료: userId={}, topic={}, partition={}, offset={}",
                    viewEventDTO.getMemberId(), topic, partition, offset);

        } catch (Exception e) {
            log.error("구매 이벤트 처리 실패: topic={}, partition={}, offset={}, key={}",
                    topic, partition, offset, key, e);
        }
    }
}

