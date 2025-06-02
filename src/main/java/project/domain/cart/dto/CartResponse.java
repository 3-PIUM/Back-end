package project.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public abstract class CartResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDTO {
        private Long itemId;
        private String itemName;
        private Integer quantity;
        private Integer price;
        private Integer totalPrice;
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartDTO {
        private Long cartId;
        private List<CartItemDTO> items;
        private Integer totalPrice;
    }
}