package project.domain.wishlist.dto;

import project.domain.item.Item;
import project.domain.itemimage.ItemImage;
import project.domain.wishlist.WishList;
import project.domain.wishlist.dto.WishListResponse.DeleteItemDTO;
import project.domain.wishlist.dto.WishListResponse.ItemSummaryDTO;
import project.domain.wishlist.dto.WishListResponse.WishListResponseDTO;
import project.global.util.ImageUtil;

import java.util.List;
import java.util.Map;

public abstract class WishListConverter {

    public static WishListResponseDTO toWishListResponseDTO(WishList wishList, ItemImage mainImage,
        String lang, double rate) {
        return WishListResponseDTO.builder()
            .wishListId(wishList.getId())
            .memberId(wishList.getMember().getId())
            .createdAt(wishList.getCreatedAt())
            .item(ItemSummaryDTO.builder()
                .itemId(wishList.getItem().getId())
                .itemName(wishList.getItem().getName(lang))
                .brand(wishList.getItem().getCompany().getName(lang))
                .originalPrice((int) (wishList.getItem().getOriginalPrice() * rate))
                .salePrice((int) (wishList.getItem().getSalePrice() * rate))
                .discountRate(wishList.getItem().getDiscountRate())
                .mainImageUrl(mainImage.getUrl())
                .wishStatus(true)
                .build())
            .build();
    }

    public static List<WishListResponseDTO> toWishListResponseDTOList(
        List<WishList> wishLists,
        Map<Long, ItemImage> itemImageMap,
        String lang,
        double rate
    ) {
        return wishLists.stream()
            .map(wishList -> toWishListResponseDTO(
                    wishList, itemImageMap.get(wishList.getItem().getId()), lang, rate
                )
            ).toList();

    }

    public static DeleteItemDTO toDeleteItemDTO(Item item, String lang) {
        return DeleteItemDTO.builder()
            .itemId(item.getId())
            .itemName(item.getName(lang))
            .build();
    }
}
