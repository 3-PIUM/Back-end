package project.domain.item.dto;

import project.domain.aisummary.AiSummary;
import project.domain.containingredient.ContainIngredient;
import project.domain.graph.Graph;
import project.domain.item.Item;
import project.domain.item.dto.ItemResponse.*;
import project.domain.itemoption.ItemOption;

import java.util.List;

public abstract class ItemConverter {

    public static ItemInfoDTO toItemInfoDTO(Item item, String mainImage, List<String> detailImages, boolean wishStatus) {
        return ItemInfoDTO.builder()
                .id(item.getId())
                .itemName(item.getName())
                .brand(item.getCompany().getName())
                .originalPrice(item.getOriginalPrice())
                .salePrice(item.getSalePrice())
                .discountRate(item.getDiscountRate())
                .options(item.getItemOptions().stream()
                        .map(ItemOption::getName)
                        .toList())
                .itemImages(ImageDTO.builder()
                        .mainImage(mainImage)
                        .detailImages(detailImages)
                        .build())
                .wishStatus(wishStatus)
                .build();
    }

    public static RiskCountDTO toRiskCountDTO(Long itemId, int safeCount, int cautionCount, int dangerCount, int noneCount) {
        return RiskCountDTO.builder()
                .id(itemId)
                .safeCount(safeCount)
                .cautionCount(cautionCount)
                .dangerCount(dangerCount)
                .noneCount(noneCount)
                .build();
    }

    public static IngredientRankingDTO toIngredientScoreDTO(Long itemId, List<ContainIngredient> containIngredient) {
        return IngredientRankingDTO.builder()
                .itemId(itemId)
                .rankingList(containIngredient.stream()
                        .map((ci) ->
                                ItemResponse.RankingSummaryDTO.builder()
                                        .id(ci.getId())
                                        .name(ci.getIngredient().getName())
                                        .effect(ci.getIngredient().getEffect())
                                        .ranking(ci.getIngredient().getRanking())
                                        .build())
                        .toList())
                .build();
    }

    public static CautionIngredientsDTO toCautionIngredientsDTO(Long itemId, List<ContainIngredient> cautionIngredients) {
        return CautionIngredientsDTO.builder()
                .itemId(itemId)
                .cautionIngredients(cautionIngredients.stream()
                        .map(ci -> IngredientSummaryDTO.builder()
                                .id(ci.getIngredient().getId())
                                .ingredientName(ci.getIngredient().getName())
                                .cautionSkinType(ci.getIngredient().getCautionSkinTypes().stream()
                                        .map(cst -> {
                                            if (cst.getSkinType() != null) {
                                                return cst.getSkinType().getString();
                                            } else {
                                                return null;
                                            }
                                        })
                                        .toList())
                                .build())
                        .toList())
                .build();
    }

    public static AiSummaryListDTO toAiSummaryListDTO(Long itemId, List<AiSummary> aiSummaries) {
        return AiSummaryListDTO.builder()
                .itemId(itemId)
                .aiSummaryList(aiSummaries.stream()
                        .map(as -> AiSummaryDTO.builder()
                                .id(as.getId())
                                .ranking(as.getRanking())
                                .title(as.getTitle())
                                .itemOption(as.getItemOption())
                                .content(as.getContent())
                                .originalContent(as.getOriginalContent())
                                .build())
                        .toList())
                .build();
    }

    public static GraphListDTO toGraphListDTO(Long itemId, List<Graph> graphs) {
        return GraphListDTO.builder()
                .itemId(itemId)
                .graphList(graphs.stream()
                        .map(gh -> GraphSummaryDTO.builder()
                                .id(gh.getId())
                                .name(gh.getName())
                                .optionName(gh.getOptionName())
                                .percentage(gh.getPercentage())
                                .build())
                        .toList())
                .build();
    }
}
