package project.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import project.domain.itemoption.ItemOption;

import java.util.List;

public abstract class CartResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class OptionDTO{
        private String selectOption;
        private List<String> options;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CartItemDTO {
        private Long cartItemId;
        private Long itemId;
        private String itemName;
        private String brand;
        private OptionDTO optionInfo;
        private String mainImageUrl;
        private Integer originalPrice;
        private Integer salePrice;
        private Integer discountRate;
        private Integer quantity;
        private Integer itemTotalPrice;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CartDTO {
        private Long cartId;
        private List<CartItemDTO> items;
        private Integer cartTotalPrice;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SummaryCartItemDTO {
        private Long itemId;
        private String itemName;
        private String option;
        private Integer originalPrice;
        private Integer salePrice;
        private Integer discountRate;
        private Integer quantity;
        private Integer itemTotalPrice;
    }
}