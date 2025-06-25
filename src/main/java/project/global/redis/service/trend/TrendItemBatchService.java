package project.global.redis.service.trend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.trenditem.TrendItem;
import project.domain.trenditem.repository.TrendItemRepository;
import project.global.redis.dto.ItemViewScoreDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrendItemBatchService {

    private static final String VIEW_COUNT_KEY = "item:view:";
    private static final String TREND_ITEMS_KEY = "trend_items";
    private final RedisTemplate<String, Object> redisTemplate;
    private final TrendItemRepository trendItemRepository;

    // 1시간마다 인기 급상승 상품 계산 및 업데이트(매시 조회수 이용)
    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional
    public void calculateTrendItems() {
        log.info("인기 급상승 상품 배치 처리 시작");

        try {
            // 인기 급상승 제품 가져오기
            List<ItemViewScoreDTO> recentTrendingItems = getRecentTrendingItems();
            // 최근 3시간 조회수 인기 급상승 상품 업데이트 - Redis
            updateRecentTrendingItemsInRedis(recentTrendingItems);
            // 최근 3시간 조회수 인기 급상승 상품 저장 - DB
            addRecentTrendingItemsInDB(recentTrendingItems);

            log.info("인기 급상승 상품 배치 처리 완료: {}개 상품 업데이트", recentTrendingItems.size());
        } catch (Exception e) {
            log.error("인기 급상승 상품 배치 처리 중 오류 발생");
        }
    }

    // itemId 추출 메소드
    private String extractItemId(String key) {
        String[] parts = key.split(":");
        return parts[parts.length - 1];
    }

    // 최근 3시간 조회수 기반 인기 급상승 상품 계산
    private List<ItemViewScoreDTO> getRecentTrendingItems() {
        log.info("인기 급상승 제품 집계 시작");
        Map<String, Double> viewScores = new HashMap<>();

        try {
            int currentHour = LocalDateTime.now().getHour();
            // 가중치 설정
            double[] weights = new double[]{1.0, 0.7, 0.5};

            for (int i = 1; i < 4; i++) {
                int targetHour = (currentHour - i + 24) % 24;
                double weight = weights[i - 1];

                // 해당 시간대 모든 키값 조회
                String pattern = VIEW_COUNT_KEY + targetHour + ":*";
                Set<String> keys = redisTemplate.keys(pattern);
                if (!keys.isEmpty()) {
                    log.debug("{}시 데이터: {}개 상품", targetHour, keys.size());

                    for (String key : keys) {
                        try {
                            // 아이템 ID 추출
                            String itemId = extractItemId(key);

                            // 조회수 가져온후 가중치 곱해서 점수 계산
                            Object viewObj = redisTemplate.opsForValue().get(key);
                            double view = viewObj != null ? Double.parseDouble(viewObj.toString()) : 0.0;
                            double viewScore = view * weight;

                            // 누적 점수 계산
                            viewScores.merge(itemId, viewScore, Double::sum);
                        } catch (Exception e) {
                            log.error("키 처리중 오류: {}", key);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("인기 급상승 제품 집계 중 오류 발생");
        }

        // 급상승 인기 제품 TOP10
        return viewScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(e -> ItemViewScoreDTO.builder()
                        .itemId(e.getKey())
                        .score(e.getValue())
                        .build())
                .limit(10)
                .toList();
    }

    // 최근 3시간 조회수 기반 인기 급상승 상품 업데이트 - Redis
    private void updateRecentTrendingItemsInRedis(List<ItemViewScoreDTO> recentTrendingItems) {
        redisTemplate.delete(TREND_ITEMS_KEY);

        int ranking = 1;
        HashMap<Object, Object> trendItems = new HashMap<>();
        for (ItemViewScoreDTO item : recentTrendingItems) {
            String key = item.getItemId();
            String value = item.getScore().toString() + ":" + ranking;
            trendItems.put(key, value);
        }

        redisTemplate.opsForHash().putAll(TREND_ITEMS_KEY, trendItems);
        redisTemplate.expire(TREND_ITEMS_KEY, Duration.ofHours(1));

        log.info("인기 급상승 제품 {}개 레디스 업데이트", recentTrendingItems.size());
    }

    // 최근 3시간 조회수 기반 인기 급상승 상품 저장 - DB
    private void addRecentTrendingItemsInDB(List<ItemViewScoreDTO> recentTrendingItems) {
        ArrayList<TrendItem> newItems = new ArrayList<>();

        int ranking = 1;
        for (ItemViewScoreDTO item : recentTrendingItems) {
            String itemId = item.getItemId();
            Double score = item.getScore();

            newItems.add(TrendItem.builder()
                    .itemId(Long.parseLong(itemId))
                    .score(score)
                    .ranking(ranking++)
                    .build());
        }

        trendItemRepository.saveAll(newItems);
        log.info("인기 급상승 제품 {}개 DB 추가", newItems.size());
    }

}
