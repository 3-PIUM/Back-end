package project.global.kafka.service.cart;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import project.global.kafka.dto.cart.CartEventDTO;

@Service
@Slf4j
public class CartLogProducer {

    @Autowired
    @Qualifier("cartKafkaTemplate")
    private KafkaTemplate<String, String> cartKafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendCartLog(CartEventDTO cartEventDTO) {
        try {
            String logData = objectMapper.writeValueAsString(cartEventDTO);
            String userId = String.valueOf(cartEventDTO.getMemberId());

            cartKafkaTemplate.sendDefault(userId, logData)
                    .whenComplete((res, ex) -> {
                        if (ex == null) {
                            log.info("장바구니 메세지 전송 성공: userId={}, topic={}, partition={}, offset={}",
                                    userId,
                                    res.getRecordMetadata().topic(),
                                    res.getRecordMetadata().partition(),
                                    res.getRecordMetadata().offset()
                            );
                        } else {
                            log.error("장바구니 메세지 전송 실패: userId={}", userId);
                            // 중요한 구매 로그이므로 실패 처리 로직
                            handleCartLogFailure(cartEventDTO, ex);
                        }
                    });

        } catch (Exception e) {
            log.error("장바구니 메세지 JSON 변환 실패", e);
            throw new RuntimeException("장바구니 메세지 처리 중 오류", e);
        }
    }

    private void handleCartLogFailure(CartEventDTO event, Throwable ex) {
        log.error("장바구니 메세지 최종 실패 처리 시작: userId={}",
                event.getMemberId());

        sendToDLQ(event);
    }

    private void sendToDLQ(CartEventDTO event) {
        try {
            String dlqData = objectMapper.writeValueAsString(event);
            cartKafkaTemplate.send("cart-events", event.getMemberId().toString(), dlqData);
            log.info("DLQ 전송 완료: userId={}",
                    event.getMemberId());
        } catch (Exception e) {
            log.error("DLQ 전송 실패: userId={}",
                    event.getMemberId(), e);
        }

    }
}
