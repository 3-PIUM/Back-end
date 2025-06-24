package project.domain.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.item.Item;
import project.domain.item.dto.ItemSearchResponse;
import project.domain.item.dto.ItemSearchResponse.TrendItemsInfoDTO;
import project.domain.item.dto.converter.ItemRecommendConverter;
import project.domain.item.repository.ItemRepository;
import project.domain.popularitem.dto.PopularItemDTO;
import project.domain.trenditem.dto.TrendItemDTO;
import project.global.response.ApiResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRecommendService {

    private final ItemRepository itemRepository;

    public ApiResponse<List<ItemSearchResponse.PopularItemsInfoDTO>> getPopularItems(List<PopularItemDTO> popularItems) {
        List<Long> popularIds = popularItems.stream()
                .map(PopularItemDTO::getItemId)
                .toList();

        List<Item> items = itemRepository.findItemByItemIdsWithMainImage(popularIds);

        List<ItemSearchResponse.PopularItemsInfoDTO> top10ItemsInfoDTOs = ItemRecommendConverter.toPopularItemsInfoDTOs(items, popularItems);
        return ApiResponse.onSuccess(top10ItemsInfoDTOs);
    }

    public ApiResponse<List<TrendItemsInfoDTO>> getTrendItems(List<TrendItemDTO> trendItems) {
        List<Long> trendIds = trendItems.stream()
                .map(TrendItemDTO::getItemId)
                .toList();

        List<Item> items = itemRepository.findItemByItemIdsWithMainImage(trendIds);

        List<TrendItemsInfoDTO> trendItemsInfoDTOs = ItemRecommendConverter.toTrendItemsInfoDTOs(items, trendItems);
        return ApiResponse.onSuccess(trendItemsInfoDTOs);
    }
}
