package project.domain.wishlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public abstract class WishListResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishListItemDTO{
        private Long itemId;
        private String itemName;
        private Integer originalPrice;
        private Integer salePrice;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishListDTO{
        private Long wishListId;
        private WishListItemDTO wishListItem;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishListInfoDTO{
        private List<WishListItemDTO> wishListItemList;
    }
}
