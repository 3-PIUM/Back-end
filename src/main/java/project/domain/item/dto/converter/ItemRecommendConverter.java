package project.domain.item.dto.converter;

import project.domain.item.Item;
import project.domain.item.dto.ItemSearchResponse.PopularItemsInfoDTO;
import project.domain.item.dto.ItemSearchResponse.TrendItemsInfoDTO;
import project.domain.popularitem.dto.PopularItemDTO;
import project.domain.trenditem.dto.TrendItemDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ItemRecommendConverter {

    public static List<PopularItemsInfoDTO> toPopularItemsInfoDTOs(List<Item> items, List<PopularItemDTO> popularItems) {
        // items를 Map으로 변환 (빠른 조회를 위해)
        Map<Long, Item> itemMap = items.stream()
                .collect(Collectors.toMap(Item::getId, item -> item));

        // popularItems 순서대로 InfoDTO 생성
        return popularItems.stream()
                .map(popularItem -> {
                    Item item = itemMap.get(popularItem.getItemId());
                    if (item == null) {
                        return null; // 해당 아이템이 없으면 null
                    }

                    return PopularItemsInfoDTO.builder()
                            .itemId(item.getId())
                            .itemName(item.getName())
                            .itemImage(!item.getItemImages().isEmpty()
                                    ? item.getItemImages().get(0).getUrl() : null)
                            .originalPrice(item.getOriginalPrice())
                            .salePrice(item.getSalePrice())
                            .discountRate(item.getDiscountRate())
                            .ranking(popularItem.getRanking())
                            .build();
                })
                .toList();
    }

    public static List<TrendItemsInfoDTO> toTrendItemsInfoDTOs(List<Item> items, List<TrendItemDTO> trendItems) {
        // items를 Map으로 변환
        Map<Long, Item> itemMap = items.stream()
                .collect(Collectors.toMap(Item::getId, item -> item));

        // trendItems 순서대로 InfoDTO 생성
        return trendItems.stream()
                .map(trendItem -> {
                    Item item = itemMap.get(trendItem.getItemId());
                    if (item == null) {
                        return null;
                    }

                    return TrendItemsInfoDTO.builder()
                            .itemId(item.getId())
                            .itemName(item.getName())
                            .itemImage(!item.getItemImages().isEmpty()
                                    ? item.getItemImages().get(0).getUrl() : null)
                            .originalPrice(item.getOriginalPrice())
                            .salePrice(item.getSalePrice())
                            .discountRate(item.getDiscountRate())
                            .ranking(trendItem.getRanking())
                            .build();
                })
                .toList();
    }
}
