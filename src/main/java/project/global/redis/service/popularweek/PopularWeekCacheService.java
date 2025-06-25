package project.global.redis.service.popularweek;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import project.domain.popularweekitem.PopularWeekItem;
import project.domain.popularweekitem.dto.PopularWeekItemConverter;
import project.domain.popularweekitem.dto.PopularWeekItemResponse.PopularWeekItemDTO;
import project.domain.popularweekitem.repository.PopularWeekItemRepository;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class PopularWeekCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String POPULAR_WEEK_ITEMS = "popular_week_items";
    private final PopularWeekItemRepository popularWeekItemRepository;

    // 최근 일주일 인기 상품 TOP20 조회(레디스 우선 조회, 없을 경우 DB 조회후 레디스 갱신)
    public List<PopularWeekItemDTO> getPopularWeekItems() {
        List<PopularWeekItemDTO> cachedData = getPopularWeekItemsFromRedis();
        if (!cachedData.isEmpty()) {
            log.debug("Redis에서 최근 일주일 인기 상품 조회: {}개", cachedData.size());
            return cachedData;
        }

        //DB에서 조회
        List<PopularWeekItem> dbItems = popularWeekItemRepository.findTop20ByOrderByCreatedAtDesc();
        if (!dbItems.isEmpty()) {
            cachePopularWeekItems(dbItems);

            List<PopularWeekItemDTO> result = PopularWeekItemConverter.toPopularWeekItemDTOs(dbItems);
            log.info("DB에서 최근 일주일 인기 상품 조회후 Redis warm-up: {}개", result.size());
            return result;
        }

        return null;
    }


    // 레디스에서 조회
    private List<PopularWeekItemDTO> getPopularWeekItemsFromRedis() {
        try{
            Set<ZSetOperations.TypedTuple<Object>> cachedData = redisTemplate.opsForZSet()
                    .reverseRangeWithScores(POPULAR_WEEK_ITEMS, 0, -1);

            if (cachedData.isEmpty()) {
                return Collections.emptyList();
            }

            return cachedData.stream()
                    .map(c -> PopularWeekItemDTO.builder()
                            .itemId(Long.parseLong(c.getValue().toString()))
                            .conversionRate(c.getScore())
                            .build())
                    .toList();

        }catch (Exception e){
            log.error("최근 일주일 인기 상품 조회중 에러 발생 - Redis");
            return Collections.emptyList();
        }
    }

    // Redis에 데이터 캐싱
    private void cachePopularWeekItems(List<PopularWeekItem> dbItems) {
        try{
            redisTemplate.delete(POPULAR_WEEK_ITEMS);

            for (PopularWeekItem item : dbItems) {
                redisTemplate.opsForZSet().add(
                        POPULAR_WEEK_ITEMS,
                        item.getItemId().toString(),
                        item.getConversionRate()
                );
            }

            redisTemplate.expire(POPULAR_WEEK_ITEMS, Duration.ofDays(1));

        }catch (Exception e){
            log.error("최근 일주일 인기 상품 캐싱중 오류 발생");
        }
    }

}
