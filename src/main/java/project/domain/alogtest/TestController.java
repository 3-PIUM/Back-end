package project.domain.alogtest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "시스템 로그 테스트")
@RestController
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Operation(
            summary = "모든 로그 테스트"
    )
    @GetMapping("/test-logs")
    public String testLogs(@Parameter(description = "임의의 로그 메세지") @RequestParam(defaultValue = "테스트") String message) {
        // 다양한 로그 레벨로 테스트
        logger.trace("TRACE 레벨 로그: {}", message);
        logger.debug("DEBUG 레벨 로그: {}", message);
        logger.info("INFO 레벨 로그: {}", message);
        logger.warn("WARN 레벨 로그: {}", message);
        logger.error("ERROR 레벨 로그: {}", message);

        // 구조화된 로그 (JSON 형태로 추가 정보 포함)
        logger.info("사용자 요청 처리 - 메시지: {}, 시간: {}, 스레드: {}",
                message,
                System.currentTimeMillis(),
                Thread.currentThread().getName());

        return "로그가 성공적으로 생성되었습니다: " + message;
    }

    @Operation(
            summary = "예외 로그 테스트"
    )
    @GetMapping("/simulate-error")
    public String simulateError() {
        try {
            // 의도적으로 예외 발생
            int result = 10 / 0;
            return "성공";
        } catch (Exception e) {
            logger.error("예외 발생: {}", e.getMessage(), e);
            return "에러가 발생했습니다. 로그를 확인하세요.";
        }
    }

    @Operation(
            summary = "로그 대량 생성"
    )
    @GetMapping("/bulk-logs")
    public String bulkLogs(@Parameter(description = "로그 개수") @RequestParam(defaultValue = "10") int count) {
        for (int i = 1; i <= count; i++) {
            logger.info("대량 로그 테스트 {}/{} - 현재 시간: {}",
                    i, count, System.currentTimeMillis());

            if (i % 3 == 0) {
                logger.warn("3의 배수 경고 로그: {}", i);
            }

            if (i % 5 == 0) {
                logger.error("5의 배수 에러 로그: {}", i);
            }
        }
        return count + "개의 로그가 생성되었습니다.";
    }
}
