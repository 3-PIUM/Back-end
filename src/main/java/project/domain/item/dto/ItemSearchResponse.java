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
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ItemSearchResultDTO {
        private int pageNumber;
        private int itemCount;
        private List<ItemSearchInfoDTO> itemSearchInfoDTOs;
    }
}
