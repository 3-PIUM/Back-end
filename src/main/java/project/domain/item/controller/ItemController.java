package project.domain.item.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.domain.item.dto.ItemResponse.*;
import project.domain.item.service.ItemService;
import project.domain.member.Member;
import project.global.redis.service.ItemViewRedis;
import project.global.response.ApiResponse;
import project.global.security.annotation.LoginMember;

@Tag(name = "상품 정보 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;
    private final ItemViewRedis itemViewRedis;

    @Operation(
            summary = "상품 정보 조회",
            description = "상품 정보 및 메인, 세부 이미지를 조회합니다."
    )
    @GetMapping("/{itemId}/info")
    public ApiResponse<ItemInfoDTO> getItemInfo(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "아이템 ID") @PathVariable Long itemId,
            @Parameter(description = "설정 언어") @RequestParam(defaultValue = "KR") String lang
    ) {
        // 조회수 증가
        itemViewRedis.incrementViewCount(itemId);

        return itemService.getItemInfo(member, itemId, lang);
    }

    @Operation(
            summary = "상품 조회수 감소"
    )
    @PostMapping("view/{itemId}/decrease")
    public ApiResponse<Void> decrementItemViewCount(
            @Parameter(description = "아이템 ID") @PathVariable Long itemId
    ) {
        // 조회수 감소
        itemViewRedis.decrementViewCount(itemId);

        return ApiResponse.OK;
    }

    @Operation(
            summary = "특정 상품 조회수 조회(실시간)"
    )
    @GetMapping("/{itemId}/view-count")
    public ApiResponse<Long> getViewCount(@PathVariable Long itemId) {
        Long viewCount = itemViewRedis.getViewCount(itemId);
        return ApiResponse.onSuccess(viewCount);
    }

    @Operation(
            summary = "성분 스코어 개수 조회",
            description = "성분 스코어(안전, 주의, 위험) 개수를 조회합니다."
    )
    @GetMapping("/{itemId}/score/count")
    public ApiResponse<RiskCountDTO> getContainIngredientScoreCount(
            @Parameter(description = "아이템 ID") @PathVariable Long itemId
    ) {
        return itemService.getRiskCount(itemId);
    }

    @Operation(
            summary = "성분 스코어 조회",
            description = "성분 스코어 정보를 조회합니다."
    )
    @GetMapping("/{itemId}/score")
    public ApiResponse<IngredientRankingDTO> getContainIngredientScore(
            @Parameter(description = "아이템 ID") @PathVariable Long itemId
    ) {
        return itemService.getContainIngredients(itemId);
    }


    @Operation(
            summary = "민감 주의 성분 조회",
            description = "민감 주의 성분 정보를 조회합니다."
    )
    @GetMapping("/{itemId}/caution")
    public ApiResponse<CautionIngredientsDTO> getCautionIngredient(
            @Parameter(description = "아이템 ID") @PathVariable Long itemId
    ) {
        return itemService.getCautionIngredients(itemId);
    }


    @Operation(
            summary = "피부 타입 별 AI 요약 조회",
            description = "피부 타입 별 AI 요약 정보를 조회합니다."
    )
    @GetMapping("/{itemId}/ai-summary")
    public ApiResponse<AiSummaryListDTO> getItemAiSummary(
            @Parameter(description = "아이템 ID") @PathVariable Long itemId
    ) {
        return itemService.getAiSummary(itemId);
    }


    @Operation(
            summary = "그래프 데이터 조회",
            description = "그래프 관련 데이터 정보를 조회합니다."
    )
    @GetMapping("/{itemId}/graph")
    public ApiResponse<GraphListDTO> getGraphData(
            @Parameter(description = "아이템 ID") @PathVariable Long itemId,
            @Parameter(description = "설정 언어") @RequestParam(defaultValue = "KR") String lang
    ) {
        return itemService.getGraphData(itemId, lang);
    }

}
