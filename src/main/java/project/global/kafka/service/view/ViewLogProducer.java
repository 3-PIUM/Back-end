package project.global.kafka.service.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import project.global.kafka.dto.view.ViewEventDTO;

@Service
@Slf4j
public class ViewLogProducer {

    @Autowired
    @Qualifier("viewKafkaTemplate")
    private KafkaTemplate<String, String> viewKafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendViewLog(ViewEventDTO viewEventDTO) {
        try {
            String logData = objectMapper.writeValueAsString(viewEventDTO);
            String userId = String.valueOf(viewEventDTO.getMemberId());

            viewKafkaTemplate.sendDefault(userId, logData)
                    .whenComplete((res, ex) -> {
                        if (ex == null) {
                            log.info("조회 메세지 전송 성공: userId={}, topic={}, partition={}, offset={}",
                                    userId,
                                    res.getRecordMetadata().topic(),
                                    res.getRecordMetadata().partition(),
                                    res.getRecordMetadata().offset()
                            );
                        } else {
                            log.error("조회 메세지 전송 실패: userId={}", userId);
                        }
                    });
        } catch (Exception e) {
            log.error("조회 메세지 JSON 변환 실패", e);
            throw new RuntimeException("조회 메세지 처리 중 오류", e);
        }
    }
}
