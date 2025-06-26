package project.domain.item.dto.converter;

import project.domain.item.Item;
import project.domain.item.dto.ItemSearchResponse.ItemSearchInfoDTO;
import project.domain.item.dto.ItemSearchResponse.ItemSearchResultDTO;

import java.util.List;

public abstract class ItemSearchConverter {

    public static ItemSearchResultDTO toItemSearchInfoDTO(List<Item> items, List<Long> wishListIds, String lang) {
        return ItemSearchResultDTO.builder()
                .itemCount(items.size())
                .itemSearchInfoDTOs(items.stream()
                        .map(i -> toItemSearchDetailInfoDTO(i, wishListIds.contains(i.getId()), lang))
                        .toList())
                .build();
    }

    public static ItemSearchInfoDTO toItemSearchDetailInfoDTO(Item item, boolean wishStatus, String lang) {
        return ItemSearchInfoDTO.builder()
                .id(item.getId())
                .itemName(item.getName(lang))
                .itemImage(!item.getItemImages().isEmpty()
                        ? item.getItemImages().get(0).getUrl() : null)
                .originalPrice(item.getOriginalPrice())
                .salePrice(item.getSalePrice())
                .discountRate(item.getDiscountRate())
                .wishStatus(wishStatus)
                .build();
    }

}
