package project.global.kafka.service.purchase;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import project.global.kafka.dto.purchase.PurchaseEventDTO;

@Service
@Slf4j
public class PurchaseLogProducer {

    @Autowired
    @Qualifier("purchaseKafkaTemplate")
    private KafkaTemplate<String, String> purchaseKafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendPurchaseLog(PurchaseEventDTO purchaseEventDTO) {
        try {
            String logData = objectMapper.writeValueAsString(purchaseEventDTO);
            String userId = String.valueOf(purchaseEventDTO.getMemberId());

            purchaseKafkaTemplate.sendDefault(userId, logData)
                    .whenComplete((res, ex) -> {
                        if (ex == null) {
                            log.info("구매 메세지 전송 성공: userId={}, topic={}, partition={}, offset={}",
                                    userId,
                                    res.getRecordMetadata().topic(),
                                    res.getRecordMetadata().partition(),
                                    res.getRecordMetadata().offset()
                            );
                        } else {
                            log.error("구매 메세지 전송 실패: userId={}",
                                    userId, ex);
                            // 중요한 구매 로그이므로 실패 처리 로직
                            handlePurchaseLogFailure(purchaseEventDTO, ex);
                        }
                    });
        } catch (Exception e) {
            log.error("구매 메세지 JSON 변환 실패", e);
            throw new RuntimeException("구매 메세지 처리 중 오류", e);
        }
    }

    private void handlePurchaseLogFailure(PurchaseEventDTO event, Throwable ex) {
        log.error("구매 메세지 최종 실패 처리 시작: userId={}",
                event.getMemberId());

        // DLQ로 전송
        sendToDLQ(event);

        // 실패 알림 발송
        sendFailureAlert(event, ex);
    }

    private void sendToDLQ(PurchaseEventDTO event) {
        try {
            String dlqData = objectMapper.writeValueAsString(event);
            purchaseKafkaTemplate.send("purchase-events", event.getMemberId().toString(), dlqData);
            log.info("DLQ 전송 완료: userId={}",
                    event.getMemberId());
        } catch (Exception e) {
            log.error("DLQ 전송 실패: userId={}",
                    event.getMemberId(), e);
        }
    }

    private void sendFailureAlert(PurchaseEventDTO event, Throwable ex) {
        // 중요한 구매 로그 실패이므로 즉시 알림
        log.error("구매 이벤트 발행 실패: userId={}, error={}",
                event.getMemberId(), ex.getMessage());
    }
}
