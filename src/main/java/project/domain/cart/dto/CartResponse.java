package project.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public abstract class CartResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CartItemDTO {
        private Long itemId;
        private String itemName;
        private String mainImageUrl;
        private Integer originalPrice;
        private Integer salePrice;
        private Integer discountRate;
        private Integer quantity;
        private Integer totalPrice;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CartDTO {
        private Long cartId;
        private List<CartItemDTO> items;
        private Integer totalPrice;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SummaryCartItemDTO {
        private Long itemId;
        private String itemName;
        private Integer originalPrice;
        private Integer salePrice;
        private Integer discountRate;
        private Integer quantity;
        private Integer totalPrice;
    }
}