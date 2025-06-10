package project.domain.wishlist.dto;

import project.domain.item.Item;
import project.domain.itemimage.ItemImage;
import project.domain.wishlist.WishList;
import project.domain.wishlist.dto.WishListResponse.DeleteItemDTO;
import project.domain.wishlist.dto.WishListResponse.ItemSummaryDTO;
import project.domain.wishlist.dto.WishListResponse.WishListResponseDTO;

import java.util.List;
import java.util.Map;

public abstract class WishListConverter {

    public static WishListResponseDTO toWishListResponseDTO(WishList wishList, ItemImage mainImage) {
        return WishListResponseDTO.builder()
                .wishListId(wishList.getId())
                .memberId(wishList.getMember().getId())
                .createdAt(wishList.getCreatedAt())
                .item(ItemSummaryDTO.builder()
                        .itemId(wishList.getItem().getId())
                        .itemName(wishList.getItem().getName())
                        .originalPrice(wishList.getItem().getOriginalPrice())
                        .salePrice(wishList.getItem().getSalePrice())
                        .discountRate(wishList.getItem().getDiscountRate())
                        .mainImageUrl(mainImage.getUrl())
                        .build())
                .build();
    }

    public static List<WishListResponseDTO> toWishListResponseDTOList(
            List<WishList> wishLists,
            Map<Long, ItemImage> itemImageMap
    ) {
        return wishLists.stream()
                .map(wishList -> WishListResponseDTO.builder()
                        .wishListId(wishList.getId())
                        .memberId(wishList.getMember().getId())
                        .createdAt(wishList.getCreatedAt())
                        .item(ItemSummaryDTO.builder()
                                .itemId(wishList.getItem().getId())
                                .itemName(wishList.getItem().getName())
                                .originalPrice(wishList.getItem().getOriginalPrice())
                                .salePrice(wishList.getItem().getSalePrice())
                                .discountRate(wishList.getItem().getDiscountRate())
                                .mainImageUrl(getMainImageUrl(wishList.getItem().getId(), itemImageMap))
                                .build())
                        .build())
                .toList();
    }

    // 해당 itemId에 맞는 메인 이미지 찾기
    private static String getMainImageUrl(Long itemId, Map<Long, ItemImage> itemImageMap) {
        ItemImage mainImage = itemImageMap.get(itemId);
        return mainImage != null ? mainImage.getUrl() : null;
    }

    public static DeleteItemDTO toDeleteItemDTO(Item item) {
        return DeleteItemDTO.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .build();
    }
}
