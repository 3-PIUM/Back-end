package project.domain.cart.dto;

import project.domain.cart.Cart;
import project.domain.cart.dto.CartResponse.CartDTO;
import project.domain.cart.dto.CartResponse.CartItemDTO;
import project.domain.cartitem.CartItem;
import project.domain.item.Item;

import java.util.List;
import java.util.stream.Collectors;

public abstract class CartConverter {

    public static CartItemDTO toCartItemDTO(CartItem cartItem) {
        Item item = cartItem.getItem();
        return CartItemDTO.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .quantity(cartItem.getQuantity())
                .price(item.getSalePrice())
                .totalPrice(item.getSalePrice() * cartItem.getQuantity())
                .build();
    }

    public static CartDTO toCartDTO(Cart cart, List<CartItem> cartItems) {
        List<CartItemDTO> cartItemDTOs = cartItems.stream()
                .map(CartConverter::toCartItemDTO)
                .collect(Collectors.toList());

        return CartDTO.builder()
                .cartId(cart.getId())
                .items(cartItemDTOs)
                .totalPrice(cart.getTotalPrice())
                .build();
    }
}
