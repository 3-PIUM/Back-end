package project.domain.item.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.domain.item.dto.ItemSearchResponse.PopularItemsInfoDTO;
import project.domain.item.dto.ItemSearchResponse.TrendItemsInfoDTO;
import project.domain.item.service.ItemRecommendService;
import project.domain.popularitem.dto.PopularItemDTO;
import project.domain.trenditem.dto.TrendItemDTO;
import project.global.redis.service.popular.PopularCacheService;
import project.global.redis.service.trend.TrendCacheService;
import project.global.response.ApiResponse;

import java.util.List;

@Tag(name = "상품 추천 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemRecommendController {

    private final PopularCacheService popularCacheService;
    private final ItemRecommendService itemRecommendService;
    private final TrendCacheService trendCacheService;

    @Operation(
            summary = "현재 인기 있는 상품 TOP 10 조회",
            description = "1시간 간격으로 업데이트 됩니다."
    )
    @GetMapping("/popular")
    public ApiResponse<List<PopularItemsInfoDTO>> getPopularItems() {
        List<PopularItemDTO> popularItems = popularCacheService.getTop10PopularItems();
        return itemRecommendService.getPopularItems(popularItems);
    }

    @Operation(
            summary = "인기 급상승 상품 TOP 10 조회",
            description = "1시간 간격으로 업데이트 됩니다."
    )
    @GetMapping("/trend")
    public ApiResponse<List<TrendItemsInfoDTO>> getTrendItems() {
        List<TrendItemDTO> trendItems = trendCacheService.getTrendItems();
        return itemRecommendService.getTrendItems(trendItems);
    }
}
