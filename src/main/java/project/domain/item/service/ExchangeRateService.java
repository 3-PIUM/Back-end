package project.domain.item.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;


@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class ExchangeRateService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String EXCHANGE_API_URL = "https://api.frankfurter.dev/v1/latest?base=KRW";

    private static final String REDIS_KEY_EN = "exchange_rate:EN";  // KRW → USD
    private static final String REDIS_KEY_JP = "exchange_rate:JP";  // KRW → JPY
    private static final long CACHE_TTL_MINUTES = 5; // 5분 캐시 유지

    public double getRate(String currency) {
        Object rateObj = redisTemplate.opsForValue().get(getRedisKey(currency));
//        log.info("Rate: {}", rateObj);
        return (rateObj instanceof Number number)
            ? number.doubleValue()
            : Optional.ofNullable(rateObj)
                .map(Object::toString)
                .map(Double::parseDouble)
                .orElse(1.0);
    }

    @Scheduled(fixedRate = 5 * 60 * 1000) // 5분마다
    public void scheduledUpdate() {
        log.info("[스케줄링] 환율 갱신 시작");
        updateRatesFromApi();
    }

    private void updateRatesFromApi() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(EXCHANGE_API_URL, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("rates")) {
                Map<String, Object> quotes = (Map<String, Object>) body.get("rates");

                Double usdToUSD = getDouble(quotes.get("USD"));
                Double usdToJpy = getDouble(quotes.get("JPY"));

                if (usdToUSD != null && usdToJpy != null) {
                    double krwToUsd = usdToUSD;
                    double krwToJpy = usdToJpy;

                    redisTemplate.opsForValue()
                        .set(REDIS_KEY_EN, krwToUsd, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
                    redisTemplate.opsForValue()
                        .set(REDIS_KEY_JP, krwToJpy, CACHE_TTL_MINUTES, TimeUnit.MINUTES);

                    log.info("[환율 갱신 성공] KRW->USD={} / KRW->JPY={}", krwToUsd, krwToJpy);
                } else {
                    log.warn("[환율 값 없음] USDKRW 또는 USDJPY 누락");
                }
            } else {
                log.warn("[API 응답 이상] quotes 없음");
            }
        } catch (Exception e) {
            log.error("[환율 API 호출 실패]", e);
        }
    }

    private Double getDouble(Object obj) {
        if (obj instanceof Number num) {
            return num.doubleValue();
        }
        try {
            return Double.parseDouble(obj.toString());
        } catch (Exception e) {
            log.warn("[환율 파싱 에러] {}", obj);
            return null;
        }
    }

    private String getRedisKey(String currency) {
        return switch (currency) {
            case "EN" -> REDIS_KEY_EN;
            case "JP" -> REDIS_KEY_JP;
            default -> "exchange_rate:DEFAULT";
        };
    }
}
