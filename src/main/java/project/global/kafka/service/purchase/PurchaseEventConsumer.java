package project.global.kafka.service.purchase;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import project.global.kafka.dto.purchase.PurchaseEventDTO;


// Consumer - 메시지 소비 및 ElasticSearch 저장
@Component
@Slf4j
public class PurchaseEventConsumer {

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
            log.info("구매 이벤트 수신: topic={}, partition={}, offset={}, key={}",
                    topic, partition, offset, key);

            // JSON -> DTO 변환
            PurchaseEventDTO purchaseEventDTO = objectMapper.readValue(eventData, PurchaseEventDTO.class);

            // ElasticSearch에 로그 저장
            purchaseLogService.savePurchaseLog(purchaseEventDTO, topic, partition, offset);

            log.info("구매 로그 저장 완료: userId={}, topic={}, partition={}, offset={}",
                    purchaseEventDTO.getMemberId(), topic, partition, offset);

        } catch (Exception e) {
            log.error("구매 이벤트 처리 실패: topic={}, partition={}, offset={}, key={}",
                    topic, partition, offset, key, e);

            // 처리 실패한 메시지 별도 처리
            handleConsumerError(eventData, topic, partition, offset, key, e);
        }
    }

    private void handleConsumerError(String eventData, String topic, int partition,
                                     long offset, String key, Exception e) {
        try {
            // 실패한 메시지를 에러 토픽으로 전송
            log.error("에러 토픽으로 전송: topic={}, partition={}, offset={}",
                    topic, partition, offset);
            // errorTopicProducer.sendErrorMessage(eventData, e);

        } catch (Exception ex) {
            log.error("에러 처리 중 추가 실패: topic={}, partition={}, offset={}",
                    topic, partition, offset, ex);
        }
    }
}
