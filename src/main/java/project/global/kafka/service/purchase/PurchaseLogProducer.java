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
        log.error("🚨 [긴급] 구매 이벤트 발행 실패: userId={}, error={}",
                event.getMemberId(), ex.getMessage());
    }

//    private void recordFailureMetric(PurchaseEventDTO event) {
//        // 모니터링 시스템에 실패 메트릭 기록
//        log.info("구매 로그 실패 메트릭 기록: userId={}, orderId={}, purchaseItemIds={}",
//                event.getUserId(), event.getPurchaseHistoryId(), event.getPurchaseIds());
//
//        // TODO: Micrometer, Prometheus 등 메트릭 시스템 연동
//        // meterRegistry.counter("purchase.log.failure",
//        //     "userId", event.getUserId(),
//        //     "productId", event.getProductId()).increment();
//    }

//    private void saveToBackupStorage(PurchaseEventDTO event) {
//        // 최후의 수단: 파일 또는 DB에 백업 저장
//        try {
//            log.error("백업 저장소에 구매 로그 저장: userId={}, orderId={}",
//                    event.getUserId(), event.getPurchaseHistoryId());
//
//            // TODO: 파일 시스템 또는 DB에 저장
//            // backupService.savePurchaseEvent(event);
//
//        } catch (Exception e) {
//            log.error("백업 저장도 실패 - 데이터 유실 위험: userId={}, orderId={}",
//                    event.getUserId(), event.getPurchaseHistoryId(), e);
//        }
//    }
}
