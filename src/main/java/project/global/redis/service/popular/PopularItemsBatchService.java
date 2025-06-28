package project.global.redis.service.popular;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.popularitem.PopularItem;
import project.domain.popularitem.repository.PopularItemRepository;
import project.global.redis.dto.ItemViewCountDTO;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PopularItemsBatchService {

    private static final String POPULAR_ITEMS_KEY = "popular_items";
    private final RedisTemplate<String, Object> redisTemplate;
    private final PopularItemRepository popularItemRepository;

    // 매 시 정각마다 인기 상품 계산 및 업데이트(누적 조회수 이용)
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void calculatePopularItems() {
        log.info("인기 상품 배치 처리 시작");

        try {
            // 모든 조회수 가져오기(1이상)
            List<ItemViewCountDTO> allViewCounts = getAllViewCounts();

            if (allViewCounts.isEmpty()) {
                log.warn("조회수 데이터가 없습니다.");
                return;
            }

            List<ItemViewCountDTO> top10Items = allViewCounts.stream()
                    .sorted((a, b)
                            -> Long.compare(b.getViewCount(), a.getViewCount()))
                    .limit(10)
                    .toList();

            // Redis 업데이트
            updatePopularItemsInRedis(top10Items);
            // DB에 저장
            addPopularItemsInDB(top10Items);

            log.info("인기 상품 배치 처리 완료: {}개 상품 업데이트", top10Items.size());
        } catch (Exception e) {
            log.error("인기 상품 배치 처리 중 오류 발생");
        }
    }


    // Redis에서 모든 조회수 조회
    private List<ItemViewCountDTO> getAllViewCounts() {
        Set<String> keys = redisTemplate.keys("item:view:*");

        if (keys.isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemViewCountDTO> viewCounts = new ArrayList<>();

        for (String key : keys) {
            try {
                String itemId = key.replace("item:view:", "");
                Object countObj = redisTemplate.opsForValue().get(key);
                long viewCount = countObj != null ?
                        Long.parseLong(countObj.toString()) : 0L;

                // 조회수가 1이상인것들만 조회
                if (viewCount > 0) {
                    viewCounts.add(ItemViewCountDTO.builder()
                            .itemId(Long.parseLong(itemId))
                            .viewCount(viewCount)
                            .build());
                }
            } catch (NumberFormatException e) {
                log.warn("잘못된 키 형식: {}", key);
            }
        }
        return viewCounts;
    }

    // Redis에 Top10 상품 업데이트
    private void updatePopularItemsInRedis(List<ItemViewCountDTO> top10Items) {
        // 기존 데이터 삭제
        redisTemplate.delete(POPULAR_ITEMS_KEY);

        for (ItemViewCountDTO item : top10Items) {
            redisTemplate.opsForZSet().add(
                    POPULAR_ITEMS_KEY,
                    item.getItemId().toString(),
                    item.getViewCount().doubleValue()
            );
        }

        redisTemplate.expire(POPULAR_ITEMS_KEY, Duration.ofHours(1));

        log.info("Redis에 인기 상품 {}개 저장 완료", top10Items.size());
    }

    // DB에 Top10 상품 정보 추가
    private void addPopularItemsInDB(List<ItemViewCountDTO> top10Items) {
        ArrayList<PopularItem> newItems = new ArrayList<>();

        int ranking = 1;
        for (ItemViewCountDTO item : top10Items) {
            newItems.add(PopularItem.builder()
                    .itemId(item.getItemId())
                    .viewCount(item.getViewCount())
                    .ranking(ranking++)
                    .build());
        }

        popularItemRepository.saveAll(newItems);
        log.info("DB에 인기 상품 {}개 추가", top10Items.size());
    }
}
