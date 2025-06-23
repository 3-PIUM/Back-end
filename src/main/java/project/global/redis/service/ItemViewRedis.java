package project.global.redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import project.domain.popularitem.dto.PopularItemConverter;
import project.domain.popularitem.dto.PopularItemDTO;
import project.domain.popularitem.PopularItem;
import project.domain.popularitem.repository.PopularItemRepository;

import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemViewRedis {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String VIEW_COUNT_KEY = "item:view:";
    private static final String POPULAR_ITEMS_KEY = "now:popular:items";
    private final PopularItemRepository popularItemRepository;

    // 조회수 증가(비동기)
    @Async
    public void incrementViewCount(Long itemId) {
        try {
            String key = VIEW_COUNT_KEY + itemId;

            // 없으면 자동으로 1로 세팅
            redisTemplate.opsForValue().increment(key);

            // Sorted Set에 점수 업데이트 (실시간 랭킹용) - 누적 점수
            // Sorted Set 키 이름, 점수 증가시킬 아이템, 증가시킬 점수
            redisTemplate.opsForZSet().incrementScore(POPULAR_ITEMS_KEY, itemId.toString(), 1.0);
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
        String key = VIEW_COUNT_KEY + itemId.toString();
        Object countObj = redisTemplate.opsForValue().get(key);
        return countObj != null ? Long.parseLong(countObj.toString()) : 0L;
    }

    // 인기 상품 TOP10 조회(레디스 조회 우선, 없을 경우 DB 조회후 레디스 갱신)
    public List<PopularItemDTO> getTop10PopularItems() {
        // 레디스 확인
        List<PopularItemDTO> cachedItems = getPopularItemsFromRedis();

        if (!cachedItems.isEmpty()) {
            log.debug("Redis에서 인기 상품 조회: {}개", cachedItems.size());
            return cachedItems;
        }

        // DB에서 조회
        List<PopularItem> dbItems = popularItemRepository.findTop10ByOrderByCreatedAtDescRankingAsc();

        if (!dbItems.isEmpty()) {
            // 레디스 캐싱
            cachePopularItemsToRedis(dbItems);

            List<PopularItemDTO> result = PopularItemConverter.toPopularItemDTOs(dbItems);
            log.info("DB에서 인기 상품 조회 후 Redis 캐시 warm-up: {} 개", result.size());
            return result;
        }

        // 둘 다 없으면 빈 리스트
        log.warn("인기 상품 데이터가 없습니다.");
        return Collections.emptyList();
    }

    // 레디스에서 인기 상품 조회
    private List<PopularItemDTO> getPopularItemsFromRedis() {
        try {
            Map<Object, Object> cachedData = redisTemplate.opsForHash().entries(POPULAR_ITEMS_KEY);

            if (cachedData.isEmpty()) {
                return Collections.emptyList();
            }

            return cachedData.entrySet().stream()
                    .map(entry -> {
                        String itemId = entry.getKey().toString();
                        String[] valueParts = entry.getValue().toString().split(":");

                        // 배열 길이 체크
                        if (valueParts.length != 2) {
                            log.warn("잘못된 캐시 데이터 형식: {}", entry.getValue());
                            return null;
                        }

                        return PopularItemDTO.builder()
                                .itemId(Long.parseLong(itemId))
                                .viewCount(Long.parseLong(valueParts[0]))
                                .ranking(Integer.parseInt(valueParts[1]))
                                .build();
                    })
                    .sorted(Comparator.comparing(PopularItemDTO::getRanking))
                    .limit(10)
                    .toList();

        } catch (Exception e) {
            log.error("Redis에서 인기 상품 조회 오류");
            return Collections.emptyList();
        }

    }

    // Redis에 인기 상품 캐싱
    private void cachePopularItemsToRedis(List<PopularItem> popularItems) {
        try {
            HashMap<String, String> cacheData = new HashMap<>();

            for (PopularItem popularItem : popularItems) {
                // 조회수:랭킹으로 value 설정
                String value = popularItem.getViewCount().toString() + ":" + popularItem.getRanking().toString();
                cacheData.put(popularItem.getItemId().toString(), value);
            }

            redisTemplate.opsForHash().putAll(POPULAR_ITEMS_KEY, cacheData);

            // 1시간 후 만료
            redisTemplate.expire(POPULAR_ITEMS_KEY, Duration.ofHours(1));

        } catch (Exception e) {
            log.error("Redis 캐싱중 오류 발생");
        }
    }
}
