package project.domain.item.dto;

import project.domain.item.Item;
import project.domain.item.dto.ItemSearchResponse.ItemSearchInfoDTO;
import project.domain.item.dto.ItemSearchResponse.ItemSearchResultDTO;
import project.domain.item.dto.ItemSearchResponse.Top10ItemsInfoDTO;

import java.util.List;

public abstract class ItemSearchConverter {

    public static ItemSearchResultDTO toItemSearchInfoDTO(List<Item> items, List<Long> wishListIds) {
        return ItemSearchResultDTO.builder()
                .itemCount(items.size())
                .itemSearchInfoDTOs(items.stream()
                        .map(i -> toItemSearchDetailInfoDTO(i, wishListIds.contains(i.getId())))
                        .toList())
                .build();
    }

    public static ItemSearchInfoDTO toItemSearchDetailInfoDTO(Item item, boolean wishStatus) {
        return ItemSearchInfoDTO.builder()
                .id(item.getId())
                .itemName(item.getName())
                .itemImage(!item.getItemImages().isEmpty()
                        ? item.getItemImages().get(0).getUrl() : null)
                .originalPrice(item.getOriginalPrice())
                .salePrice(item.getSalePrice())
                .discountRate(item.getDiscountRate())
                .wishStatus(wishStatus)
                .build();
    }

    public static List<Top10ItemsInfoDTO> toTop10ItemsInfoDTOs(List<Item> items) {
        return items.stream()
                .map(i -> Top10ItemsInfoDTO.builder()
                        .itemId(i.getId())
                        .itemName(i.getName())
                        .itemImage(!i.getItemImages().isEmpty()
                                ? i.getItemImages().get(0).getUrl() : null)
                        .originalPrice(i.getOriginalPrice())
                        .salePrice(i.getSalePrice())
                        .discountRate(i.getDiscountRate())
                        .build())
                .toList();
    }
}
