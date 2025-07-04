package project.global.redis.service.popular;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import project.domain.popularitem.PopularItem;
import project.domain.popularitem.dto.PopularItemConverter;
import project.domain.popularitem.dto.PopularItemDTO;
import project.domain.popularitem.repository.PopularItemRepository;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopularCacheService {

    private final PopularItemRepository popularItemRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String POPULAR_ITEMS_KEY = "popular_items";

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
            Object jsonString = redisTemplate.opsForValue().get(POPULAR_ITEMS_KEY);

            if (jsonString != null) {
                JavaType listType = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, PopularItem.class);

                List<PopularItem> popularItems = objectMapper.readValue(
                        jsonString.toString(), listType
                );

                return PopularItemConverter.toPopularItemDTOs(popularItems);
            }
        } catch (Exception e) {
            log.error("Redis에서 인기 상품 조회 오류");
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }

    // Redis에 인기 상품 캐싱
    private void cachePopularItemsToRedis(List<PopularItem> popularItems) {
        try {
            redisTemplate.delete(POPULAR_ITEMS_KEY);

            for (PopularItem popularItem : popularItems) {
                redisTemplate.opsForZSet().add(
                        POPULAR_ITEMS_KEY, popularItem.getItemId(), popularItem.getViewCount());
            }

            // 1시간 후 만료
            redisTemplate.expire(POPULAR_ITEMS_KEY, Duration.ofHours(1));

        } catch (Exception e) {
            log.error("Redis 캐싱중 오류 발생");
        }
    }
}
