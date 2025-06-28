package project.global.redis.service.trend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import project.domain.trenditem.TrendItem;
import project.domain.trenditem.dto.TrendItemConverter;
import project.domain.trenditem.dto.TrendItemDTO;
import project.domain.trenditem.repository.TrendItemRepository;

import java.time.Duration;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrendCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String TREND_ITEMS_KEY = "trend_items";
    private final TrendItemRepository trendItemRepository;

    // 인기 급상승 상품 Top10 조회(레디스 우선 조회, 없을 경우 DB 조회후 레디스 갱신)
    public List<TrendItemDTO> getTrendItems() {

        List<TrendItemDTO> trendItemsFromRedis = getTrendItemsFromRedis();
        if (!trendItemsFromRedis.isEmpty()) {
            log.debug("Redis에서 인기 급상승 상품 조회: {}개", trendItemsFromRedis.size());
            return trendItemsFromRedis;
        }

        List<TrendItem> dbItems = trendItemRepository.findTop10ByOrderByCreatedAtDescRankingAsc();
        if (!dbItems.isEmpty()) {
            cacheTrendItems(dbItems);

            List<TrendItemDTO> trendItemDTOs = TrendItemConverter.toTrendItemDTOs(dbItems);
            log.info("DB에서 인기 상품 조회 후 Redis 캐시 warm-up: {} 개", trendItemDTOs.size());
            return trendItemDTOs;
        }


        log.warn("인기 급상승 상품 데이터가 없습니다.");
        return Collections.emptyList();
    }

    // Redis에서 인기 급상승 제품 조회
    private List<TrendItemDTO> getTrendItemsFromRedis() {
        try {
            Set<ZSetOperations.TypedTuple<Object>> cachedData = redisTemplate.opsForZSet().reverseRangeWithScores(TREND_ITEMS_KEY, 0, -1);
            List<ZSetOperations.TypedTuple<Object>> dataList = new ArrayList<>(cachedData);

            return IntStream.range(0, dataList.size())
                    .mapToObj(i -> {
                        ZSetOperations.TypedTuple<Object> cd = dataList.get(i);
                        return TrendItemDTO.builder()
                                .itemId(Long.parseLong(cd.getValue().toString()))
                                .score(cd.getScore().doubleValue())
                                .ranking(i + 1)
                                .build();
                    }).toList();

        } catch (Exception e) {
            log.error("Redis에서 인기 급상승 제품 조회 중 오류");
            return Collections.emptyList();
        }
    }

    // DB에서 조회후 Redis 갱신
    private void cacheTrendItems(List<TrendItem> trendItems) {
        try {
            redisTemplate.delete(TREND_ITEMS_KEY);

            for (TrendItem trendItem : trendItems) {
                redisTemplate.opsForZSet().add(
                        TREND_ITEMS_KEY, trendItem.getItemId().toString(), trendItem.getScore());
            }

            redisTemplate.expire(TREND_ITEMS_KEY, Duration.ofHours(1));

        } catch (Exception e) {
            log.error("Redis 캐싱중 오류 발생");
        }
    }

}
