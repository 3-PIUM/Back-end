package project.domain.cart.dto;

import project.domain.cart.Cart;
import project.domain.cart.dto.CartResponse.CartDTO;
import project.domain.cart.dto.CartResponse.CartItemDTO;
import project.domain.cart.dto.CartResponse.SummaryCartItemDTO;
import project.domain.cartitem.CartItem;
import project.domain.item.Item;
import project.domain.itemimage.ItemImage;

import java.util.List;
import java.util.Map;

public abstract class CartConverter {
    public static SummaryCartItemDTO toSummaryCartItemDTO(CartItem cartItem) {
        Item item = cartItem.getItem();
        return SummaryCartItemDTO.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .originalPrice(item.getOriginalPrice())
                .salePrice(item.getSalePrice())
                .quantity(cartItem.getQuantity())
                .totalPrice(item.getSalePrice() * cartItem.getQuantity())
                .build();
    }

    public static CartItemDTO toCartItemDTO(CartItem cartItem, ItemImage mainImage) {
        Item item = cartItem.getItem();
        return CartItemDTO.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .originalPrice(item.getOriginalPrice())
                .salePrice(item.getSalePrice())
                .mainImageUrl(mainImage.getUrl())
                .quantity(cartItem.getQuantity())
                .totalPrice(item.getSalePrice() * cartItem.getQuantity())
                .build();
    }

    public static CartDTO toCartDTO(
            Cart cart,
            List<CartItem> cartItems,
            Map<Long, ItemImage> itemImages
    ) {
        return CartDTO.builder()
                .cartId(cart.getId())
                .items(cartItems.stream()
                        .map(item ->
                                CartItemDTO.builder()
                                        .itemId(item.getId())
                                        .itemName(item.getItem().getName())
                                        .originalPrice(item.getItem().getOriginalPrice())
                                        .salePrice(item.getItem().getSalePrice())
                                        .mainImageUrl(CartConverter.getMainImageUrl(
                                                item.getItem().getId(), itemImages
                                        ))
                                        .quantity(item.getQuantity())
                                        .totalPrice(item.getItem().getSalePrice() * item.getQuantity())
                                        .build()
                        ).toList()
                )
                .totalPrice(cart.getTotalPrice())
                .build();
    }

    // 해당 itemId에 맞는 메인 이미지 찾기
    private static String getMainImageUrl(Long itemId, Map<Long, ItemImage> itemImageMap) {
        ItemImage mainImage = itemImageMap.get(itemId);
        return mainImage != null ? mainImage.getUrl() : null;
    }
}
