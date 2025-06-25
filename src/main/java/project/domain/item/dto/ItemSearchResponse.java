package project.domain.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public abstract class ItemSearchResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ItemSearchInfoDTO{
        private Long id;
        private String itemName;
        private int originalPrice;
        private int salePrice;
        private int discountRate;
        private String itemImage;
        private boolean wishStatus;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ItemSearchResultDTO {
        private int itemCount;
        private List<ItemSearchInfoDTO> itemSearchInfoDTOs;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PopularItemsInfoDTO {
        private Long itemId;
        private String itemName;
        private int originalPrice;
        private int salePrice;
        private int discountRate;
        private String itemImage;
        private Integer ranking;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TrendItemsInfoDTO {
        private Long itemId;
        private String itemName;
        private int originalPrice;
        private int salePrice;
        private int discountRate;
        private String itemImage;
        private Integer ranking;
    }
}
