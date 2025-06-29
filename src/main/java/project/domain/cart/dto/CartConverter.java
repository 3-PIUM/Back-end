package project.domain.cart.dto;

import project.domain.cart.Cart;
import project.domain.cart.dto.CartResponse.CartDTO;
import project.domain.cart.dto.CartResponse.CartItemDTO;
import project.domain.cart.dto.CartResponse.OptionDTO;
import project.domain.cart.dto.CartResponse.SummaryCartItemDTO;
import project.domain.cartitem.CartItem;
import project.domain.item.Item;
import project.domain.itemimage.ItemImage;
import project.domain.itemoption.ItemOption;
import project.global.util.ImageUtil;

import java.util.List;
import java.util.Map;

public abstract class CartConverter {

    public static SummaryCartItemDTO toSummaryCartItemDTO(CartItem cartItem, String lang) {
        Item item = cartItem.getItem();
        return SummaryCartItemDTO.builder()
            .itemId(cartItem.getId())
            .itemName(item.getName(lang))
            .option(cartItem.getItemOption())
            .originalPrice(item.getOriginalPrice())
            .salePrice(item.getSalePrice())
            .quantity(cartItem.getQuantity())
            .itemTotalPrice(item.getSalePrice() * cartItem.getQuantity())
            .build();
    }

    public static CartItemDTO toCartItemDTO(CartItem cartItem, ItemImage mainImage, String lang) {
        Item item = cartItem.getItem();
        return CartItemDTO.builder()
            .cartItemId(cartItem.getId())
            .itemName(item.getName(lang))
            .optionInfo(OptionDTO.builder()
                .selectOption(cartItem.getItemOption())
                .options(cartItem.getItem().getItemOptions().stream()
                    .map(itemOption -> itemOption.getName(lang))
                    .toList())
                .build())
            .originalPrice(item.getOriginalPrice())
            .salePrice(item.getSalePrice())
            .mainImageUrl(mainImage.getUrl())
            .quantity(cartItem.getQuantity())
            .itemTotalPrice(item.getSalePrice() * cartItem.getQuantity())
            .build();
    }

    public static CartDTO toCartDTO(
        Cart cart,
        List<CartItem> cartItems,
        Map<Long, ItemImage> itemImages,
        String lang
    ) {
        return CartDTO.builder()
            .cartId(cart.getId())
            .items(cartItems.stream()
                .map(ci ->
                    CartItemDTO.builder()
                        .cartItemId(ci.getId())
                        .itemId(ci.getItem().getId())
                        .itemName(ci.getItem().getName(lang))
                        .brand(ci.getItem().getCompany().getName(lang))
                        .optionInfo(OptionDTO.builder()
                            .selectOption(ci.getItemOption())
                            .options(ci.getItem().getItemOptions().stream()
                                .map(itemOption -> itemOption.getName(lang))
                                .toList())
                            .build())
                        .originalPrice(ci.getItem().getOriginalPrice())
                        .salePrice(ci.getItem().getSalePrice())
                        .mainImageUrl(ImageUtil.getMainImageUrl(
                            ci.getItem().getId(), itemImages
                        ))
                        .quantity(ci.getQuantity())
                        .discountRate(ci.getItem().getDiscountRate())
                        .itemTotalPrice(ci.getItem().getSalePrice() * ci.getQuantity())
                        .build()
                ).toList()
            )
            .cartTotalPrice(cart.getTotalPrice())
            .build();
    }
}
