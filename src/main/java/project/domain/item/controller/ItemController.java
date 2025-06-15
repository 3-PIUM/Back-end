package project.domain.item.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.domain.item.dto.ItemResponse.*;
import project.domain.item.service.ItemService;
import project.global.response.ApiResponse;

@Tag(name = "상품 정보 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    @Operation(
            summary = "상품 정보 조회",
            description = "상품 정보 및 메인, 세부 이미지를 조회합니다."
    )
    @GetMapping("/{itemId}/info")
    public ApiResponse<ItemInfoDTO> getItemInfo(
            @Parameter(description = "아이템 ID") @PathVariable Long itemId
    ) {
        return itemService.getItemInfo(itemId);
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
            @Parameter(description = "아이템 ID") @PathVariable Long itemId
    ) {
        return itemService.getGraphData(itemId);
    }
}
