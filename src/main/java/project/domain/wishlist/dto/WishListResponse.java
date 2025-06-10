package project.domain.wishlist.dto;

import lombok.*;
import project.domain.itemimage.ItemImage;

import java.time.LocalDateTime;
import java.util.List;

public abstract class WishListResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DeleteItemDTO {
        private Long itemId;
        private String itemName;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class WishListResponseDTO {
        private Long wishListId;
        private Long memberId;
        private LocalDateTime createdAt;
        private ItemSummaryDTO item;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ItemSummaryDTO {
        private Long itemId;
        private String itemName;
        private String mainImageUrl;
        private Integer originalPrice;
        private Integer salePrice;
        private Integer discountRate;
    }
}
