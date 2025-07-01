package project.global.redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import project.domain.item.repository.ItemRepository;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemViewRedis {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ItemRepository itemRepository;
    private static final String VIEW_COUNT_KEY = "item:view:";
    private static final String CUMULATIVE_VIEWS_KEY = "cumulative_views";

    // 조회수 증가(비동기)
    @Async
    public void incrementViewCount(Long itemId) {
        try {
            // 먼저 Redis에서 아이템 존재 여부 확인
            String itemExistsKey = "item:exists:" + itemId;
            Object o = redisTemplate.opsForValue().get(itemExistsKey);
            String exists = o == null ? null : o.toString();

            if (exists == null) {
                // 캐시에 없으면 DB 조회 (한 번만)
                if (!itemRepository.existsById(itemId)) {
                    return; // 존재하지 않으면 조회수 증가 안함
                }
                // 존재하면 캐시에 저장 (TTL 설정)
                redisTemplate.opsForValue().set(itemExistsKey, "true", Duration.ofHours(1));
            }

            // 실시간 조회수 저장
            String key = VIEW_COUNT_KEY + itemId;
            redisTemplate.opsForValue().decrement(key);
            redisTemplate.expire(key, Duration.ofHours(1));

            // 1시간 단위로 누적 조회수 저장
            String hourlyKey = VIEW_COUNT_KEY + LocalDateTime.now().getHour() + ":" + itemId;
            redisTemplate.opsForValue().increment(hourlyKey);
            redisTemplate.expire(hourlyKey, Duration.ofHours(4));

            // Sorted Set에 점수 업데이트 (실시간 랭킹용) - 하루 누적 조회수
            redisTemplate.opsForZSet().incrementScore(CUMULATIVE_VIEWS_KEY, itemId.toString(), 1.0);
            // 자정까지 남은 시간 계산해서 하루 한번만 TTL 설정
            Long currentTTL = redisTemplate.getExpire(CUMULATIVE_VIEWS_KEY);
            if (currentTTL == null || currentTTL == -1) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime midnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
                Duration untilMidnight = Duration.between(now, midnight);
                redisTemplate.expire(CUMULATIVE_VIEWS_KEY, untilMidnight);

                log.debug("하루 누적 조회 - TTL 설정: {}", untilMidnight);
            }

        } catch (Exception e) {
            log.error("조회수 증가 실패");
        }
    }

    // 조회수 감소
    public void decrementViewCount(Long itemId) {
        String key = VIEW_COUNT_KEY + itemId;
        redisTemplate.opsForValue().decrement(key);
    }

    // 특정 상품 조회수 가져오기
    public Long getViewCount(Long itemId) {
        String key = VIEW_COUNT_KEY + itemId;
        Object countObj = redisTemplate.opsForValue().get(key);
        return countObj != null ? Long.parseLong(countObj.toString()) : 0L;
    }
}
