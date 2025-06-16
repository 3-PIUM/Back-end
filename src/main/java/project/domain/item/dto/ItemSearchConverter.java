package project.domain.item.dto;

import project.domain.item.Item;
import project.domain.item.dto.ItemSearchResponse.ItemSearchInfoDTO;
import project.domain.item.dto.ItemSearchResponse.ItemSearchResultDTO;

import java.util.List;

public abstract class ItemSearchConverter {

    public static ItemSearchResultDTO toItemSearchInfoDTO(List<Item> items, int pageNumber) {
        return ItemSearchResultDTO.builder()
                .pageNumber(pageNumber)
                .itemCount(items.size())
                .itemSearchInfoDTOs(items.stream()
                        .map(ItemSearchConverter::toItemSearchInfoDTO)
                        .toList())
                .build();
    }

    public static ItemSearchInfoDTO toItemSearchInfoDTO(Item item) {
        return ItemSearchInfoDTO.builder()
            .id(item.getId())
            .itemName(item.getName())
            .itemImage(!item.getItemImages().isEmpty()
                ? item.getItemImages().get(0).getUrl() : null)
            .originalPrice(item.getOriginalPrice())
            .salePrice(item.getSalePrice())
            .discountRate(item.getDiscountRate())
            .build();
    }

}
