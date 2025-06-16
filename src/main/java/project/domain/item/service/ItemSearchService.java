package project.domain.item.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.category.Category;
import project.domain.category.repository.CategoryRepository;
import project.domain.item.Item;
import project.domain.item.dto.ItemSearchConverter;
import project.domain.item.dto.ItemSearchResponse.ItemSearchInfoDTO;
import project.domain.item.dto.ItemSearchResponse.ItemSearchResultDTO;
import project.domain.item.enums.VeganType;
import project.domain.item.repository.ItemRepository;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemSearchService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    /*
        서브 카테고리 별 아이템 조회
         */
    public ApiResponse<ItemSearchResultDTO> getItemsBySubCategory(String subCategoryName,
        Pageable pageable) {

        // 아이템 정보와 메인 이미지를 한번에 조회
        Page<Item> items = itemRepository.findBySubCategoryNameWithMainImage(subCategoryName,
            pageable);
        if (items.isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.ITEM_NOT_FOUND, null);
        }

        ItemSearchResultDTO itemSearchResultDTO = ItemSearchConverter.toItemSearchInfoDTO(
            items.getContent(), items.getNumber());
        return ApiResponse.onSuccess(itemSearchResultDTO);
    }

    /*
          카테고리 별 구매내역순서 아이템 조회
     */
    public ApiResponse<List<ItemSearchInfoDTO>> getItemsByCategoryOrderByCount(
        String categoryName) {
        Long categoryId;
        if (categoryName == null || categoryName.isEmpty()) {
            categoryId = null;
        } else {
            Category category = categoryRepository.findByName(categoryName).orElseThrow(
                () -> new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND));
            categoryId = category.getId();
        }

        List<Item> top10ItemsByCategory = itemRepository.findTop10ItemsByCategory(
            categoryId);

        List<ItemSearchInfoDTO> itemSearchInfoDTO = top10ItemsByCategory.stream()
            .map(ItemSearchConverter::toItemSearchDetailInfoDTO).toList();

        return ApiResponse.onSuccess(itemSearchInfoDTO);
    }

    /*
    검색 키워드에 맞는 아이템 조회
     */
    public ApiResponse<ItemSearchResultDTO> getItemsByKeyword(String keyword, Pageable pageable) {

        // 아이템 정보와 메인 이미지를 한번에 조회
        Page<Item> items = itemRepository.findByKeywordWithMainImage(keyword, pageable);
        if (items.isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.ITEM_NOT_FOUND, null);
        }

        ItemSearchResultDTO itemSearchResultDTO = ItemSearchConverter.toItemSearchInfoDTO(
            items.getContent(), items.getNumber());
        return ApiResponse.onSuccess(itemSearchResultDTO);
    }

    /*
    비건 제품 조회
     */
    public ApiResponse<ItemSearchResultDTO> getVeganItems(String subCategory, Pageable pageable) {

        // 아이템 정보와 메인 이미지를 한번에 조회
        Page<Item> veganItems = itemRepository.findByVeganTypeWithMainImage(VeganType.VEGAN,
            subCategory, pageable);
        if (veganItems.isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.ITEM_NOT_FOUND, null);
        }

        ItemSearchResultDTO itemSearchResultDTO = ItemSearchConverter.toItemSearchInfoDTO(
            veganItems.getContent(), veganItems.getNumber());
        return ApiResponse.onSuccess(itemSearchResultDTO);
    }
}
