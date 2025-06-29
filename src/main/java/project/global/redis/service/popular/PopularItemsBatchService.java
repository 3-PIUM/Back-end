package project.global.redis.service.popular;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.popularitem.PopularItem;
import project.domain.popularitem.repository.PopularItemRepository;
import project.global.redis.dto.ItemViewCountDTO;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PopularItemsBatchService {

    private static final String CUMULATIVE_VIEWS_KEY = "cumulative_views";
    private static final String POPULAR_ITEMS_KEY = "popular_items";
    private final RedisTemplate<String, Object> redisTemplate;
    private final PopularItemRepository popularItemRepository;
    private final ObjectMapper objectMapper;

    // 15분마다 인기 상품 계산 및 업데이트(누적 조회수 이용)
    @Scheduled(cron = "0 0,15,30,45 * * * *")
    @Transactional
    public void calculatePopularItems() {
        log.info("인기 상품 배치 처리 시작");

        try {
            // 누적 조회수로 인기 제품 top10 조회
            List<ItemViewCountDTO> popularItems = getPopularItems();

            if (popularItems.size() < 10) {
                log.warn("누적 조회수 데이터가 아직 충분하지 않습니다.");
                return;
            }

            // Redis 업데이트
            updateTop10ItemsToRedis(popularItems);

            // DB에 저장
            addPopularItemsInDB(popularItems);

            log.info("인기 상품 배치 처리 완료: {}개 상품 업데이트", popularItems.size());
        } catch (Exception e) {
            log.error("인기 상품 배치 처리 중 오류 발생");
        }
    }


    // Redis에서 누적 조회수로 Top10 조회
    private List<ItemViewCountDTO> getPopularItems() {
        List<ItemViewCountDTO> viewCounts = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<Object>> zset = redisTemplate.opsForZSet()
                .reverseRangeWithScores(CUMULATIVE_VIEWS_KEY, 0, 9);

        zset.forEach(s -> {
            try {
                String itemId = s.getValue().toString();
                Double score = s.getScore();

                viewCounts.add(ItemViewCountDTO.builder()
                        .itemId(Long.parseLong(itemId))
                        .viewCount(score.longValue())
                        .build());
            } catch (Exception e) {
                log.warn("잘못된 itemId 형식: {}", s.getValue());
            }
        });

        return viewCounts;
    }

    // Redis에 Top10 아이템을 JSON으로 저장
    private void updateTop10ItemsToRedis(List<ItemViewCountDTO> top10Items) {
        try {
            // ranking 정보 추가한 DTO 리스트 생성
            List<PopularItem> redisItems = new ArrayList<>();

            for (int i = 0; i < top10Items.size(); i++) {
                ItemViewCountDTO item = top10Items.get(i);
                redisItems.add(PopularItem.builder()
                        .itemId(item.getItemId())
                        .viewCount(item.getViewCount())
                        .ranking(i + 1)
                        .build());
            }

            // JSON으로 직렬화하여 저장
            String jsonString = objectMapper.writeValueAsString(redisItems);
            redisTemplate.opsForValue().set(POPULAR_ITEMS_KEY, jsonString, Duration.ofMinutes(20));

            log.debug("Redis에 Top10 아이템 JSON 저장 완료: {}개", redisItems.size());
        } catch (Exception e) {
            log.error("Redis Top10 아이템 저장 실패", e);
        }
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
