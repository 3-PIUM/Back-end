package project.domain.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

public abstract class ItemRecommendResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PopularItemSummaryDTO {
        private Long itemId;
        private String itemName;
        private int originalPrice;
        private int salePrice;
        private int discountRate;
        private String itemImage;
        private Integer ranking;
        private boolean wishStatus;
    }

    @Data
    @Builder
    public static class PopularItemsInfoDTO {
        private String title;
        private List<PopularItemSummaryDTO> popularItems;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TrendItemSummaryDTO {
        private Long itemId;
        private String itemName;
        private int originalPrice;
        private int salePrice;
        private int discountRate;
        private String itemImage;
        private Integer ranking;
        private boolean wishStatus;
    }

    @Data
    @Builder
    public static class TrendItemsInfoDTO {
        private String title;
        private List<TrendItemSummaryDTO> popularItems;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PopularWeekItemSummaryDTO {
        private Long itemId;
        private String itemName;
        private int originalPrice;
        private int salePrice;
        private int discountRate;
        private String itemImage;
        private boolean wishStatus;
    }

    @Data
    @Builder
    public static class PopularWeekItemsInfoDTO {
        private String title;
        private List<PopularWeekItemSummaryDTO> popularItems;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AreaPopularItemSummaryDTO {
        private Long itemId;
        private String itemName;
        private int originalPrice;
        private int salePrice;
        private int discountRate;
        private String itemImage;
        private boolean wishStatus;
    }

    @Data
    @Builder
    public static class AreaPopularItemsInfoDTO {
        private String title;
        private List<AreaPopularItemSummaryDTO> popularItems;
    }

    @Data
    @Builder
    public static class RelatedPurchaseItemDTO {
        private Long itemId;
        private String itemName;
        private int originalPrice;
        private int salePrice;
        private int discountRate;
        private String itemImage;
        private boolean wishStatus;
    }

    @Data
    @Builder
    public static class RelatedViewItemDTO {
        private Long itemId;
        private String itemName;
        private int originalPrice;
        private int salePrice;
        private int discountRate;
        private String itemImage;
        private boolean wishStatus;
    }
}
