package project.global.redis.service.popularweek;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.popularweekitem.PopularWeekItem;
import project.domain.popularweekitem.repository.PopularWeekItemRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PopularWeekItemsBatchService {

    private final ElasticsearchClient searchElasticsearchClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String POPULAR_WEEK_ITEMS = "popular_week_items";
    private final PopularWeekItemRepository popularWeekItemRepository;


    // 매주 월요일 정각에 최근 일주일간 인기 상품 계산
    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void calculatePopularWeekItems() {
        log.info("최근 일주일간 인기 상품 배치 처리 시작");

        try {
            // 최근 일주일간 각 상품별 장바구니에 담긴수 집계
            Map<Long, Integer> weeklyViewStats = getWeeklyCartStats();
            // 최근 일주일간 각 상품별 구매수 집계
            Map<Long, Integer> weeklyPurchaseStats = getWeeklyPurchaseStats();

            log.info("구매 통계 집계 시작");

            if (weeklyViewStats.isEmpty() || weeklyPurchaseStats.isEmpty()) {
                log.warn("장바구니수 또는 구매수 데이터가 없습니다.");
                return;
            }

            // 구매 전환율 계산 후 상위 20개 조회
            HashMap<Long, Double> purchaseConversions = getPurchaseConversions(weeklyViewStats, weeklyPurchaseStats);
            if (purchaseConversions.isEmpty()) {
                log.warn("구매 전환율을 계산한 데이터가 없습니다.");
                return;
            }

            // 상위 20개만 조회
            List<Map.Entry<Long, Double>> top20PurchaseConversionItemIds = purchaseConversions.entrySet().stream()
                    .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                    .limit(20)
                    .toList();

            // 레디스 업데이트
            updatePopularWeekItemsInRedis(top20PurchaseConversionItemIds);
            // DB 추가
            updatePopularWeekItemsInDB(top20PurchaseConversionItemIds);

            log.info("최근 일주일간 인기 상품 {}개 집계 완료",
                    top20PurchaseConversionItemIds.size());
        } catch (Exception e) {
            log.error("최근 일주일간 인기 상품 배치 처리중 오류 발생");
        }
    }


    // 구매 전환율 계산
    private HashMap<Long, Double> getPurchaseConversions(
            Map<Long, Integer> weeklyViewStats,
            Map<Long, Integer> weeklyPurchaseStats
    ) {
        HashMap<Long, Double> purchaseConversions = new HashMap<>();

        try {
            // 집계할 id 조회 - 중복 제거를 위해 Set 사용
            Set<Long> itemIds = new HashSet<>();
            itemIds.addAll(weeklyViewStats.keySet());
            itemIds.addAll(weeklyPurchaseStats.keySet());

            for (Long itemId : itemIds) {
                Integer viewCount = weeklyViewStats.getOrDefault(itemId, 0);
                Integer purchaseCount = weeklyPurchaseStats.getOrDefault(itemId, 0);

                double purchaseConversion = viewCount > 0 ?
                        ((double) purchaseCount / viewCount * 100) : 0.0;

                purchaseConversions.put(itemId, purchaseConversion);
            }

            log.info("구매 전환율 {}개 상품 완료", itemIds.size());
        } catch (Exception e) {
            log.error("구매 전환율 계산중 오류 발생");
        }

        return purchaseConversions;
    }


    // 최근 일주일 날짜 추출 메소드
    private List<String> getRecentWeekDates() {
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        for (int i = 7; i >= 1; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(pattern));
        }

        return dates;
    }


    /**
     * 장바구니 통계 집계
     */
    private Map<Long, Integer> getWeeklyCartStats() {
        Map<Long, Integer> viewStats = new HashMap<>();

        try {
            List<String> dates = getRecentWeekDates();
            String allIndices = dates.stream()
                    .map(date -> "cart-events-" + date)
                    .collect(Collectors.joining(","));

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(allIndices)
                    .size(0)
                    .query(q -> q.matchAll(m -> m))
                    .aggregations("items", a -> a
                            .terms(t -> t
                                    .field("item_id")
                                    .size(10000)
                            )
                    )
            );

            SearchResponse<Object> response = searchElasticsearchClient.search(searchRequest, Object.class);

            // 아이템별 장바구니 집계
            if (response.aggregations() != null) {
                Aggregate itemsAgg = response.aggregations().get("items");

                if (itemsAgg != null && itemsAgg.isLterms()) {
                    LongTermsAggregate terms = itemsAgg.lterms();

                    for (LongTermsBucket bucket : terms.buckets().array()) {
                        try {
                            Long itemId = bucket.key();
                            int totalViews = (int) bucket.docCount();

                            viewStats.put(itemId, totalViews);
                        } catch (Exception e) {
                            log.warn("장바구니 통계 처리 실패: {}", bucket.key(), e);
                        }
                    }
                }
            }

            log.info("장바구니 통계 집계 완료 - {}개 상품", viewStats.size());
        } catch (Exception e) {
            log.error("장바구니 통계 집계 실패", e);
        }

        return viewStats;
    }

    /**
     * 구매 통계 집계
     */
    private Map<Long, Integer> getWeeklyPurchaseStats() {
        Map<Long, Integer> purchaseStats = new HashMap<>();

        try {
            List<String> dates = getRecentWeekDates();
            String allIndices = dates.stream()
                    .map(date -> "purchase-events-" + date)
                    .collect(Collectors.joining(","));

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(allIndices)
                    .size(0)
                    .query(q -> q.matchAll(m -> m))
                    .aggregations("items", a -> a
                            .terms(t -> t
                                    .field("purchase_item_ids.keyword")
                                    .size(10000)
                            )
                    )
            );

            SearchResponse<Object> response = searchElasticsearchClient.search(searchRequest, Object.class);

            if (response.aggregations() != null) {
                Aggregate itemsAgg = response.aggregations().get("items");

                if (itemsAgg != null && itemsAgg.isSterms()) {
                    StringTermsAggregate terms = itemsAgg.sterms();

                    for (StringTermsBucket bucket : terms.buckets().array()) {
                        try {
                            Long itemId = Long.parseLong(bucket.key().stringValue());
                            int totalPurchases = (int) bucket.docCount();

                            purchaseStats.put(itemId, totalPurchases);
                        } catch (NumberFormatException e) {
                            log.warn("구매 통계 처리 실패: {}", bucket.key(), e);
                        }
                    }
                }
            }

            log.info("구매 통계 집계 완료 - {}개 상품", purchaseStats.size());
        } catch (Exception e) {
            log.error("구매 통계 집계 실패", e);
        }

        return purchaseStats;
    }

    // Redis 업데이트
    private void updatePopularWeekItemsInRedis(List<Map.Entry<Long, Double>> top20PurchaseConversionItemIds) {
        redisTemplate.delete(POPULAR_WEEK_ITEMS);
        top20PurchaseConversionItemIds.forEach(e -> {
            Long itemId = e.getKey();
            Double conversion = e.getValue();
            redisTemplate.opsForZSet().add(
                    POPULAR_WEEK_ITEMS, itemId.toString(), conversion);
        });
        redisTemplate.expire(POPULAR_WEEK_ITEMS, Duration.ofDays(1));
    }

    // DB 저장
    private void updatePopularWeekItemsInDB(List<Map.Entry<Long, Double>> top20PurchaseConversionItemIds) {
        ArrayList<PopularWeekItem> newItems = new ArrayList<>();

        for (Map.Entry<Long, Double> item : top20PurchaseConversionItemIds) {
            PopularWeekItem newItem = PopularWeekItem.builder()
                    .itemId(item.getKey())
                    .conversionRate(item.getValue())
                    .build();

            newItems.add(newItem);
        }

        popularWeekItemRepository.saveAll(newItems);
        log.info("DB에 최근 일주일 인기 상품 {}개 추가",
                top20PurchaseConversionItemIds.size());
    }

}
