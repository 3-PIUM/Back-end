package project.domain.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.item.Item;
import project.domain.item.dto.ItemRecommendResponse;
import project.domain.item.dto.ItemRecommendResponse.PopularItemsInfoDTO;
import project.domain.item.dto.ItemRecommendResponse.PopularWeekItemsInfoDTO;
import project.domain.item.dto.ItemRecommendResponse.TrendItemsInfoDTO;
import project.domain.item.dto.converter.ItemRecommendConverter;
import project.domain.item.repository.ItemRepository;
import project.domain.member.Member;
import project.domain.popularitem.dto.PopularItemDTO;
import project.domain.popularweekitem.dto.PopularWeekItemResponse.PopularWeekItemDTO;
import project.domain.trenditem.dto.TrendItemDTO;
import project.domain.wishlist.repository.WishlistRepository;
import project.global.response.ApiResponse;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRecommendService {

    private final ItemRepository itemRepository;
    private final WishlistRepository wishlistRepository;

    // 누적 조회수로 찾은 인기 상품 top10
    public ApiResponse<PopularItemsInfoDTO> getPopularItems(Member member, List<PopularItemDTO> popularItems, String lang) {
        List<Long> wishListIds = getWishListIds(member);

        List<Long> popularIds = popularItems.stream()
                .map(PopularItemDTO::getItemId)
                .toList();

        List<Item> items = itemRepository.findItemByItemIdsWithMainImage(popularIds);
        String title = "베스트 오브 베스트";

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
        String title = "지금 주목할만한 인기 상품";

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
        String title = "위클리 베스트";

        PopularWeekItemsInfoDTO popularWeekItemsInfoDTO = ItemRecommendConverter.toPopularWeekItemsInfoDTOs(
                title, items, popularWeekItems, wishListIds, lang);
        return ApiResponse.onSuccess(popularWeekItemsInfoDTO);
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
