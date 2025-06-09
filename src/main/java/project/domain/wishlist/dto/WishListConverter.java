package project.domain.wishlist.dto;

import lombok.RequiredArgsConstructor;
import project.domain.item.Item;
import project.domain.wishlist.WishList;
import project.domain.wishlist.dto.WishListResponse.WishListDTO;
import project.domain.wishlist.dto.WishListResponse.WishListInfoDTO;
import project.domain.wishlist.dto.WishListResponse.WishListItemDTO;

import java.util.List;

@RequiredArgsConstructor
public abstract class WishListConverter {

    public static WishListItemDTO toWishListItemDTO(Item item) {
        return WishListItemDTO.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .originalPrice(item.getOriginalPrice())
                .salePrice(item.getSalePrice())
                .build();
    }

    public static WishListInfoDTO toWishListDTO(List<WishList> wishLists) {
        List<WishListItemDTO> wishListItemDTOs = wishLists.stream()
                .map(w->toWishListItemDTO(w.getItem()))
                .toList();

        return WishListResponse.WishListInfoDTO.builder()
                .wishListItemList(wishListItemDTOs)
                .build();
    }
}
