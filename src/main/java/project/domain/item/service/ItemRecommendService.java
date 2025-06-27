package project.domain.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.areapopularitem.AreaPopularItem;
import project.domain.areapopularitem.repository.AreaPopularItemRepository;
import project.domain.item.Item;
import project.domain.item.dto.ItemRecommendResponse.*;
import project.domain.item.dto.converter.ItemRecommendConverter;
import project.domain.item.repository.ItemRepository;
import project.domain.member.Member;
import project.domain.member.enums.Area;
import project.domain.member.repository.MemberRepository;
import project.domain.popularitem.dto.PopularItemDTO;
import project.domain.popularweekitem.dto.PopularWeekItemResponse.PopularWeekItemDTO;
import project.domain.relatedpurchaseitem.repository.RelatedPurchaseItemRepository;
import project.domain.relatedviewitem.repository.RelatedViewItemRepository;
import project.domain.trenditem.dto.TrendItemDTO;
import project.domain.wishlist.repository.WishlistRepository;
import project.global.response.ApiResponse;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRecommendService {

    private final ItemRepository itemRepository;
    private final WishlistRepository wishlistRepository;
    private final AreaPopularItemRepository areaPopularItemRepository;
    private final RelatedPurchaseItemRepository relatedPurchaseItemRepository;
    private final RelatedViewItemRepository relatedViewItemRepository;
    private final MemberRepository memberRepository;

    // 누적 조회수로 찾은 인기 상품 top10
    public ApiResponse<PopularItemsInfoDTO> getPopularItems(Member member, List<PopularItemDTO> popularItems, String lang) {
        List<Long> wishListIds = getWishListIds(member);

        List<Long> popularIds = popularItems.stream()
                .map(PopularItemDTO::getItemId)
                .toList();

        List<Item> items = itemRepository.findItemByItemIdsWithMainImage(popularIds);

        String value = lang.toUpperCase();
        String title = switch (value) {
            case "EN" -> "BEST OF BEST";
            case "JP" -> "ベスト・オブ・ベスト";
            default -> "베스트 오브 베스트";
        };

        PopularItemsInfoDTO top10ItemsInfoDTOs =
                ItemRecommendConverter.toPopularItemsInfoDTOs(
                        title, items, popularItems, wishListIds, lang);
        return ApiResponse.onSuccess(top10ItemsInfoDTOs);
    }

    // 최근 3시간 조회수*가중치로 찾은 인기 급상승 상품 top10
    public ApiResponse<TrendItemsInfoDTO> getTrendItems(Member member, List<TrendItemDTO> trendItems, String lang) {
        List<Long> wishListIds = getWishListIds(member);

        List<Long> trendIds = trendItems.stream()
                .map(TrendItemDTO::getItemId)
                .toList();

        List<Item> items = itemRepository.findItemByItemIdsWithMainImage(trendIds);

        String value = lang.toUpperCase();
        String title = switch (value) {
            case "EN" -> "Trending Items to Watch Now";
            case "JP" -> "今注目の人気商品";
            default -> "지금 주목할만한 인기 상품";
        };

        TrendItemsInfoDTO trendItemsInfoDTOs =
                ItemRecommendConverter.toTrendItemsInfoDTOs(
                        title, items, trendItems, wishListIds, lang);
        return ApiResponse.onSuccess(trendItemsInfoDTOs);
    }

    // 매주 월요일 정각에 구매 전환율을 계산하여 찾은 인기 상품 20개
    public ApiResponse<PopularWeekItemsInfoDTO> getPopularWeekItems(Member member, List<PopularWeekItemDTO> popularWeekItems, String lang) {
        List<Long> wishListIds = getWishListIds(member);

        List<Long> popularWeekIds = popularWeekItems.stream()
                .map(PopularWeekItemDTO::getItemId)
                .toList();

        List<Item> items = itemRepository.findItemByItemIdsWithMainImage(popularWeekIds);

        String value = lang.toUpperCase();
        String title = switch (value) {
            case "EN" -> "WEEKLY OF BEST";
            case "JP" -> "ウィークリーベスト";
            default -> "위클리 베스트";
        };

        PopularWeekItemsInfoDTO popularWeekItemsInfoDTO = ItemRecommendConverter.toPopularWeekItemsInfoDTOs(
                title, items, popularWeekItems, wishListIds, lang);
        return ApiResponse.onSuccess(popularWeekItemsInfoDTO);
    }

    // 국가별 추천 상품 Top 30 - 데이터는 ML에서 처리해줌
    public ApiResponse<AreaPopularItemsInfoDTO> getAreaPopularItems(Member member, String lang) {
        Area area = member == null ? Area.KOREA : member.getArea();

        String value = lang.toUpperCase();
        String title = switch (value) {
            case "EN" -> "Hottest Products in the U.S. Right Now";
            case "JP" -> "今、日本で最も話題の製品";
            default -> "지금 한국에서 가장 핫한 제품";
        };

        List<Long> wishListIds = getWishListIds(member);

        List<AreaPopularItem> areaPopularItems = areaPopularItemRepository.findTop30ByAreaOrderByCreatedAtDesc(area);
        List<Long> areaPopularItemIds = areaPopularItems.stream()
                .map(AreaPopularItem::getItemId)
                .toList();

        List<Item> items = itemRepository.findItemByItemIdsWithMainImage(areaPopularItemIds);

        AreaPopularItemsInfoDTO areaPopularItemsInfoDTO = ItemRecommendConverter.toAreaPopularItemsInfoDTOs(
                title, items, areaPopularItems, wishListIds, lang);

        return ApiResponse.onSuccess(areaPopularItemsInfoDTO);
    }

    // 장바구니 담은 상품과 주로 함께 구매된 상품 조회
    public ApiResponse<List<RelatedPurchaseItemDTO>> getRelatedPurchaseItems(Member member, Long itemId, String lang) {
        List<Long> wishListIds = getWishListIds(member);

        Member mem = member != null ? member : memberRepository.findRandom();

        // 고객층 - FEMALE_건성_10대
        String customerSegment = mem.getGender().toString() + "_" + mem.getSkinType().getString() + "_" + mem.getAgeGroup();

        List<Long> relatedPurchaseItemIds = relatedPurchaseItemRepository
                .find9RandomItemIdsBySkinTypeAndCustomerSegment(itemId, mem.getSkinType().toString(), customerSegment);
        if (relatedPurchaseItemIds.isEmpty()) {
            relatedPurchaseItemIds = relatedPurchaseItemRepository.find9RandomItemIds();
        }

        List<Item> items = itemRepository.findItemByItemIdsWithMainImage(relatedPurchaseItemIds);


        List<RelatedPurchaseItemDTO> relatedPurchaseItemDTOs =
                ItemRecommendConverter.toRelatedPurchaseItemDTOs(items, wishListIds, lang);
        return ApiResponse.onSuccess(relatedPurchaseItemDTOs);
    }

    // 다른 고객이 보고 있는 상품과 함께 본 상품 조회
    public ApiResponse<List<RelatedViewItemDTO>> getRelatedViewItems(Member member, Long itemId, String lang) {
        List<Long> wishListIds = getWishListIds(member);

        Member mem = member != null ? member : memberRepository.findRandom();

        // 고객층 - FEMALE_10대_CHINA
        String customerSegment = mem.getGender().toString() + "_" + mem.getAgeGroup() + "_" + mem.getArea().toString();

        List<Long> relatedViewItemIds = relatedViewItemRepository
                .find12RandomRelatedItemIdsByItemIdAndCustomerSegment(itemId, customerSegment);
        if (relatedViewItemIds.isEmpty()) {
            relatedViewItemIds = relatedPurchaseItemRepository.find9RandomItemIds();
        }


        List<Item> items = itemRepository.findItemByItemIdsWithMainImage(relatedViewItemIds);


        List<RelatedViewItemDTO> relatedViewItemDTOs =
                ItemRecommendConverter.toRelatedViewItemDTOs(items, wishListIds, lang);
        return ApiResponse.onSuccess(relatedViewItemDTOs);
    }


    private List<Long> getWishListIds(Member member) {
        List<Long> wishListIds = new ArrayList<>();
        if (member != null) {
            List<Long> fetchedIds = wishlistRepository.findByMemberId(member.getId())
                    .stream()
                    .map(w -> w.getItem().getId())
                    .toList();
            wishListIds.addAll(fetchedIds);
        }
        return wishListIds;
    }

}
