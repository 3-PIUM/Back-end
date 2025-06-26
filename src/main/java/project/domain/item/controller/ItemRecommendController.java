package project.domain.item.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.domain.item.dto.ItemRecommendResponse;
import project.domain.item.dto.ItemRecommendResponse.*;
import project.domain.item.service.ItemRecommendService;
import project.domain.member.Member;
import project.domain.popularitem.dto.PopularItemDTO;
import project.domain.popularweekitem.dto.PopularWeekItemResponse.PopularWeekItemDTO;
import project.domain.trenditem.dto.TrendItemDTO;
import project.global.redis.service.popular.PopularCacheService;
import project.global.redis.service.popularweek.PopularWeekCacheService;
import project.global.redis.service.trend.TrendCacheService;
import project.global.response.ApiResponse;
import project.global.security.annotation.LoginMember;

import java.util.List;

@Tag(name = "상품 추천 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemRecommendController {

    private final PopularCacheService popularCacheService;
    private final ItemRecommendService itemRecommendService;
    private final TrendCacheService trendCacheService;
    private final PopularWeekCacheService popularWeekCacheService;

    @Operation(
            summary = "현재 인기 있는 상품 TOP 10 조회",
            description = "1시간 간격으로 업데이트 됩니다."
    )
    @GetMapping("/popular")
    public ApiResponse<PopularItemsInfoDTO> getPopularItems(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "설정 언어") @RequestParam(defaultValue = "KR") String lang
    ) {
        List<PopularItemDTO> popularItems = popularCacheService.getTop10PopularItems();
        return itemRecommendService.getPopularItems(member, popularItems, lang);
    }

    @Operation(
            summary = "인기 급상승 상품 TOP 10 조회",
            description = "1시간 간격으로 업데이트 됩니다."
    )
    @GetMapping("/trend")
    public ApiResponse<TrendItemsInfoDTO> getTrendItems(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "설정 언어") @RequestParam(defaultValue = "KR") String lang
    ) {
        List<TrendItemDTO> trendItems = trendCacheService.getTrendItems();
        return itemRecommendService.getTrendItems(member, trendItems, lang);
    }

    @Operation(
            summary = "최근 일주일 인기 상품 TOP 20 조회",
            description = "매일 정각에 업데이트 됩니다."
    )
    @GetMapping("/popularWeek")
    public ApiResponse<PopularWeekItemsInfoDTO> getPopularWeekItems(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "설정 언어") @RequestParam(defaultValue = "KR") String lang
    ) {
        List<PopularWeekItemDTO> popularWeekItems = popularWeekCacheService.getPopularWeekItems();
        return itemRecommendService.getPopularWeekItems(member, popularWeekItems, lang);
    }

    @Operation(
            summary = "국가별 TOP 30 조회"
    )
    @GetMapping("/areaPopular")
    public ApiResponse<AreaPopularItemsInfoDTO> getAreaPopularItems(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "설정 언어") @RequestParam(defaultValue = "KR") String lang
    ) {
        return itemRecommendService.getAreaPopularItems(member, lang);
    }

    @Operation(
            summary = "장바구니 담은 상품과 주로 함께 구매된 상품 조회"
    )
    @GetMapping("/relatedPurchaseItems/{itemId}")
    public ApiResponse<List<RelatedPurchaseItemDTO>> getRelatedPurchaseItems(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "아이템 ID") @PathVariable Long itemId,
            @Parameter(description = "설정 언어") @RequestParam(defaultValue = "KR") String lang
    ) {
        return itemRecommendService.getRelatedPurchaseItems(member, itemId, lang);
    }

    @Operation(
            summary = "다른 고객이 보고 있는 상품과 함께 본 상품 조회"
    )
    @GetMapping("/relatedViewItems/{itemId}")
    public ApiResponse<List<RelatedViewItemDTO>> getRelatedViewItems(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "아이템 ID") @PathVariable Long itemId,
            @Parameter(description = "설정 언어") @RequestParam(defaultValue = "KR") String lang
    ) {
        return itemRecommendService.getRelatedViewItems(member, itemId, lang);
    }
}
