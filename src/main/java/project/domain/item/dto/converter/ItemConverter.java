package project.domain.item.dto.converter;

import lombok.RequiredArgsConstructor;
import project.domain.aisummary.AiSummary;
import project.domain.containingredient.ContainIngredient;
import project.domain.graph.Graph;
import project.domain.item.Item;
import project.domain.item.dto.ItemResponse;
import project.domain.item.dto.ItemResponse.*;
import project.domain.item.service.ExchangeRateService;
import project.domain.itemoption.ItemOption;
import project.domain.member.enums.Language;

import java.util.List;

@RequiredArgsConstructor
public abstract class ItemConverter {

    public static ItemInfoDTO toItemInfoDTO(Item item, String mainImage, List<String> detailImages,
        boolean wishStatus, String lang,double rate) {
        return ItemInfoDTO.builder()
            .id(item.getId())
            .itemName(item.getName(lang))
            .brand(item.getCompany().getName(lang))
            .originalPrice((int)(item.getOriginalPrice()*rate))
            .salePrice((int)(item.getSalePrice()*rate))
            .discountRate(item.getDiscountRate())
            .options(item.getItemOptions().stream()
                .map(itemOption -> itemOption.getName(lang))
                .toList())
            .itemImages(ImageDTO.builder()
                .mainImage(mainImage)
                .detailImages(detailImages)
                .build())
            .wishStatus(wishStatus)
            .build();
    }

    public static RiskCountDTO toRiskCountDTO(Long itemId, int safeCount, int cautionCount,
        int dangerCount, int noneCount) {
        return RiskCountDTO.builder()
            .id(itemId)
            .safeCount(safeCount)
            .cautionCount(cautionCount)
            .dangerCount(dangerCount)
            .noneCount(noneCount)
            .build();
    }

    public static IngredientRankingDTO toIngredientScoreDTO(Long itemId,
        List<ContainIngredient> containIngredient, String lang) {
        return IngredientRankingDTO.builder()
            .itemId(itemId)
            .rankingList(containIngredient.stream()
                .map((ci) ->
                    ItemResponse.RankingSummaryDTO.builder()
                        .id(ci.getId())
                        .name(ci.getIngredient().getName(lang))
                        .effect(ci.getIngredient().getEffect(lang))
                        .ranking(ci.getIngredient().getRanking())
                        .build())
                .toList())
            .build();
    }

    public static CautionIngredientsDTO toCautionIngredientsDTO(Long itemId,
        List<ContainIngredient> cautionIngredients, String lang) {
        return CautionIngredientsDTO.builder()
            .itemId(itemId)
            .cautionIngredients(cautionIngredients.stream()
                .map(ci -> IngredientSummaryDTO.builder()
                    .id(ci.getIngredient().getId())
                    .ingredientName(ci.getIngredient().getName(lang))
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

    public static AiSummaryListDTO toAiSummaryListDTO(Long itemId, List<AiSummary> aiSummaries,
        String lang) {
        return AiSummaryListDTO.builder()
            .itemId(itemId)
            .aiSummaryList(aiSummaries.stream()
                .map(as -> AiSummaryDTO.builder()
                    .id(as.getId())
                    .ranking(as.getRanking())
                    .title(as.getTitle(lang))
                    .itemOption(as.getItemOption(lang))
                    .content(as.getContent(lang))
                    .originalContent(as.getOriginalContent(lang))
                    .build())
                .toList())
            .build();
    }

    public static GraphListDTO toGraphListDTO(Long itemId, List<Graph> graphs, String lang) {
        return GraphListDTO.builder()
            .itemId(itemId)
            .graphList(graphs.stream()
                .map(gh -> GraphSummaryDTO.builder()
                    .id(gh.getId())
                    .name(gh.getName(lang))
                    .optionName(gh.getOptionName(lang))
                    .percentage(gh.getPercentage())
                    .build())
                .toList())
            .build();
    }
}
