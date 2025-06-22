package project.domain.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import project.domain.category.Category;
import project.domain.category.repository.CategoryRepository;
import project.domain.item.Item;
import project.domain.item.dto.ItemSearchConverter;
import project.domain.item.dto.ItemSearchResponse.ItemSearchInfoDTO;
import project.domain.item.dto.ItemSearchResponse.ItemSearchResultDTO;
import project.domain.item.enums.VeganType;
import project.domain.item.repository.ItemRepository;
import project.domain.member.Member;
import project.domain.wishlist.WishList;
import project.domain.wishlist.repository.WishlistRepository;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemSearchService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final WishlistRepository wishlistRepository;

    /*
        서브 카테고리 별 아이템 조회
         */
    public ApiResponse<ItemSearchResultDTO> getItemsBySubCategory(
            String subCategoryName,
            Member member,
            String skinIssue
    ) {
        // 찜한 상품 Id
        List<Long> wishListIds = wishlistRepository.findByMemberId(member.getId())
                .stream()
                .map(w -> w.getItem().getId())
                .toList();

        // 아이템 정보와 메인 이미지를 한번에 조회
        List<Item> items;
        if (StringUtils.hasText(skinIssue)) {
            // 스킨이슈에 해당하는 제품 조회
            items = itemRepository.findBySubCategoryNameAndSkinIssueWithMainImage(subCategoryName, skinIssue);
        } else {
            // 전체조회
            items = itemRepository.findBySubCategoryNameWithMainImage(subCategoryName);
        }
        if (items.isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.ITEM_NOT_FOUND, null);
        }

        ItemSearchResultDTO itemSearchResultDTO = ItemSearchConverter.toItemSearchInfoDTO(items, wishListIds);
        return ApiResponse.onSuccess(itemSearchResultDTO);
    }

    /*
          카테고리 별 구매내역순서 아이템 조회
     */
    public ApiResponse<List<ItemSearchInfoDTO>> getItemsByCategoryOrderByCount(
            Member member,
            String categoryName) {
        // 찜한 상품 Id
        List<WishList> wishLists = wishlistRepository.findByMemberId(member.getId());
        List<Long> wishListIds = wishLists.stream()
                .map(w -> w.getItem().getId())
                .toList();

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
                .map(ti ->
                        ItemSearchConverter.toItemSearchDetailInfoDTO(ti, wishListIds.contains(ti.getId())))
                .toList();

        return ApiResponse.onSuccess(itemSearchInfoDTO);
    }

    /*
    검색 키워드에 맞는 아이템 조회
     */
    public ApiResponse<ItemSearchResultDTO> getItemsByKeyword(Member member, String keyword) {

        // 찜한 상품 Id
        List<WishList> wishLists = wishlistRepository.findByMemberId(member.getId());
        List<Long> wishListIds = wishLists.stream()
                .map(w -> w.getItem().getId())
                .toList();

        // 아이템 정보와 메인 이미지를 한번에 조회
        List<Item> items = itemRepository.findByKeywordWithMainImage(keyword);
        if (items.isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.ITEM_NOT_FOUND, null);
        }

        ItemSearchResultDTO itemSearchResultDTO = ItemSearchConverter.toItemSearchInfoDTO(items, wishListIds);
        return ApiResponse.onSuccess(itemSearchResultDTO);
    }

    /*
    비건 제품 조회
     */
    public ApiResponse<ItemSearchResultDTO> getVeganItems(Member member, String subCategory, String skinIssue) {

        // 찜한 상품 Id
        List<WishList> wishLists = wishlistRepository.findByMemberId(member.getId());
        List<Long> wishListIds = wishLists.stream()
                .map(w -> w.getItem().getId())
                .toList();

        // 아이템 정보와 메인 이미지를 한번에 조회
        List<Item> veganItems;
        if (StringUtils.hasText(skinIssue)) {
            veganItems = itemRepository.findByVeganItemsAndSkinIssueWithMainImage(subCategory, skinIssue);
        }else{
            veganItems = itemRepository.findByVeganTypeWithMainImage(subCategory);
        }
        if (veganItems.isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.ITEM_NOT_FOUND, null);
        }

        ItemSearchResultDTO itemSearchResultDTO = ItemSearchConverter.toItemSearchInfoDTO(
                veganItems, wishListIds);
        return ApiResponse.onSuccess(itemSearchResultDTO);
    }

}
