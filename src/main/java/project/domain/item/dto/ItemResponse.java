package project.domain.item.dto;

import lombok.*;

import java.util.List;

public abstract class ItemResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ImageDTO{
        private String mainImage;
        private List<String> detailImages;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ItemInfoDTO{
        private Long id;
        private String itemName;
        private String brand;
        private int originalPrice;
        private int salePrice;
        private int discountRate;
        private List<String> options;
        private ImageDTO itemImages;
        private boolean wishStatus;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RiskCountDTO {
        private Long id;
        private int safeCount;
        private int cautionCount;
        private int dangerCount;
        private int noneCount;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RankingSummaryDTO {
        private Long id;
        private String name;
        private String effect;
        private int ranking;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class IngredientRankingDTO {
        private Long itemId;
        private List<RankingSummaryDTO> rankingList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class IngredientSummaryDTO {
        private Long id;
        private String ingredientName;
        private List<String> cautionSkinType;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CautionIngredientsDTO {
        private Long itemId;
        private List<IngredientSummaryDTO> cautionIngredients;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AiSummaryDTO {
        private Long id;
        private int ranking;
        private String title;
        private String content;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AiSummaryListDTO {
        private Long itemId;
        private List<AiSummaryDTO> aiSummaryList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class GraphSummaryDTO {
        private Long id;
        private String name;
        private String optionName;
        private int percentage;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class GraphListDTO {
        private Long itemId;
        private List<GraphSummaryDTO> graphList;
    }
}
