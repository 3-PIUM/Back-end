package project.domain.item.dto.converter;

import project.domain.areapopularitem.AreaPopularItem;
import project.domain.item.Item;
import project.domain.item.dto.ItemRecommendResponse;
import project.domain.item.dto.ItemRecommendResponse.*;
import project.domain.popularitem.dto.PopularItemDTO;
import project.domain.popularweekitem.dto.PopularWeekItemResponse;
import project.domain.popularweekitem.dto.PopularWeekItemResponse.PopularWeekItemDTO;
import project.domain.relatedpurchaseitem.RelatedPurchaseItem;
import project.domain.trenditem.dto.TrendItemDTO;
import project.domain.wishlist.WishList;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ItemRecommendConverter {

    public static PopularItemsInfoDTO toPopularItemsInfoDTOs(
        String title, List<Item> items, List<PopularItemDTO> popularItems, List<Long> wishListIds,
        String lang, double rate) {
        // items를 Map으로 변환 (빠른 조회를 위해)
        Map<Long, Item> itemMap = items.stream()
            .collect(Collectors.toMap(Item::getId, item -> item));

        // popularItems 순서대로 InfoDTO 생성
        return PopularItemsInfoDTO.builder()
            .title(title)
            .popularItems(popularItems.stream()
                .map(popularItem -> {
                    Item item = itemMap.get(popularItem.getItemId());
                    if (item == null) {
                        return null; // 해당 아이템이 없으면 null
                    }

                    return PopularItemSummaryDTO.builder()
                        .itemId(item.getId())
                        .itemName(item.getName(lang))
                        .itemImage(!item.getItemImages().isEmpty()
                            ? item.getItemImages().get(0).getUrl() : null)
                        .originalPrice((int)(item.getOriginalPrice() * rate))
                        .salePrice((int)(item.getSalePrice() * rate))
                        .discountRate(item.getDiscountRate())
                        .ranking(popularItem.getRanking())
                        .wishStatus(wishListIds.contains(popularItem.getItemId()))
                        .build();
                })
                .toList())
            .build();
    }

    public static TrendItemsInfoDTO toTrendItemsInfoDTOs(
        String title, List<Item> items, List<TrendItemDTO> trendItems, List<Long> wishListIds,
        String lang, double rate) {
        // items를 Map으로 변환
        Map<Long, Item> itemMap = items.stream()
            .collect(Collectors.toMap(Item::getId, item -> item));

        // trendItems 순서대로 InfoDTO 생성
        return TrendItemsInfoDTO.builder()
            .title(title)
            .popularItems(trendItems.stream()
                .map(trendItem -> {
                    Item item = itemMap.get(trendItem.getItemId());
                    if (item == null) {
                        return null; // 해당 아이템이 없으면 null
                    }

                    return TrendItemSummaryDTO.builder()
                        .itemId(item.getId())
                        .itemName(item.getName(lang))
                        .itemImage(!item.getItemImages().isEmpty()
                            ? item.getItemImages().get(0).getUrl() : null)
                        .originalPrice((int)(item.getOriginalPrice() * rate))
                        .salePrice((int)(item.getSalePrice() * rate))
                        .discountRate(item.getDiscountRate())
                        .ranking(trendItem.getRanking())
                        .wishStatus(wishListIds.contains(trendItem.getItemId()))
                        .build();
                })
                .toList())
            .build();
    }

    public static PopularWeekItemsInfoDTO toPopularWeekItemsInfoDTOs(
        String title, List<Item> items, List<PopularWeekItemDTO> popularWeekItems,
        List<Long> wishListIds, String lang, double rate) {
        // items를 Map으로 변환
        Map<Long, Item> itemMap = items.stream()
            .collect(Collectors.toMap(Item::getId, item -> item));

        // trendItems 순서대로 InfoDTO 생성
        return PopularWeekItemsInfoDTO.builder()
            .title(title)
            .popularItems(popularWeekItems.stream()
                .map(popularWeekItem -> {
                    Item item = itemMap.get(popularWeekItem.getItemId());
                    if (item == null) {
                        return null; // 해당 아이템이 없으면 null
                    }

                    return PopularWeekItemSummaryDTO.builder()
                        .itemId(item.getId())
                        .itemName(item.getName(lang))
                        .itemImage(!item.getItemImages().isEmpty()
                            ? item.getItemImages().get(0).getUrl() : null)
                        .originalPrice((int)(item.getOriginalPrice() * rate))
                        .salePrice((int)(item.getSalePrice() * rate))
                        .discountRate(item.getDiscountRate())
                        .wishStatus(wishListIds.contains(popularWeekItem.getItemId()))
                        .build();
                })
                .toList())
            .build();
    }

    public static AreaPopularItemsInfoDTO toAreaPopularItemsInfoDTOs(
        String title, List<Item> items, List<AreaPopularItem> areaPopularItems,
        List<Long> wishListIds, String lang, double rate) {
        // items를 Map으로 변환
        Map<Long, Item> itemMap = items.stream()
            .collect(Collectors.toMap(Item::getId, item -> item));

        return AreaPopularItemsInfoDTO.builder()
            .title(title)
            .popularItems(areaPopularItems.stream()
                .map(popularWeekItem -> {
                    Item item = itemMap.get(popularWeekItem.getItemId());
                    if (item == null) {
                        return null; // 해당 아이템이 없으면 null
                    }

                    return AreaPopularItemSummaryDTO.builder()
                        .itemId(item.getId())
                        .itemName(item.getName(lang))
                        .itemImage(!item.getItemImages().isEmpty()
                            ? item.getItemImages().get(0).getUrl() : null)
                        .originalPrice((int)(item.getOriginalPrice() * rate))
                        .salePrice((int)(item.getSalePrice() * rate))
                        .discountRate(item.getDiscountRate())
                        .wishStatus(wishListIds.contains(popularWeekItem.getItemId()))
                        .build();
                })
                .toList())
            .build();
    }

    public static List<RelatedPurchaseItemDTO> toRelatedPurchaseItemDTOs(
        List<Item> items, List<Long> wishListIds, String lang, double rate) {
        return items.stream()
            .map(i -> RelatedPurchaseItemDTO.builder()
                .itemId(i.getId())
                .itemName(i.getName(lang))
                .itemImage(!i.getItemImages().isEmpty()
                    ? i.getItemImages().get(0).getUrl() : null)
                .originalPrice((int)(i.getOriginalPrice() * rate))
                .salePrice((int)(i.getSalePrice() * rate))
                .discountRate(i.getDiscountRate())
                .wishStatus(wishListIds.contains(i.getId()))
                .build())
            .toList();
    }

    public static List<RelatedViewItemDTO> toRelatedViewItemDTOs(
        List<Item> items, List<Long> wishListIds, String lang, double rate) {
        return items.stream()
            .map(i -> RelatedViewItemDTO.builder()
                .itemId(i.getId())
                .itemName(i.getName(lang))
                .itemImage(!i.getItemImages().isEmpty()
                    ? i.getItemImages().get(0).getUrl() : null)
                .originalPrice((int)(i.getOriginalPrice() * rate))
                .salePrice((int)(i.getSalePrice() * rate))
                .discountRate(i.getDiscountRate())
                .wishStatus(wishListIds.contains(i.getId()))
                .build())
            .toList();
    }
}
