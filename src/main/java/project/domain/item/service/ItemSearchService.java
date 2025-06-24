package project.domain.item.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.category.Category;
import project.domain.category.repository.CategoryRepository;
import project.domain.item.Item;
import project.domain.item.dto.converter.ItemSearchConverter;
import project.domain.item.dto.ItemSearchResponse.ItemSearchInfoDTO;
import project.domain.item.dto.ItemSearchResponse.ItemSearchResultDTO;
import project.domain.item.repository.ItemDynamicSort;
import project.domain.item.repository.ItemRepository;
import project.domain.member.Member;
import project.domain.wishlist.WishList;
import project.domain.wishlist.repository.WishlistRepository;
import project.global.elasticsearch.document.ItemDocument;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemSearchService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final WishlistRepository wishlistRepository;
    private final ItemDynamicSort itemDynamicSort;
    private final ElasticsearchClient searchElasticsearchClient;

    /*
        서브 카테고리 별 아이템 조회
         */
    public ApiResponse<ItemSearchResultDTO> getItemsBySubCategory(
            String subCategoryName,
            Member member,
            String skinIssue,
            String priceSort
    ) {
        // 찜한 상품 Id
        List<Long> wishListIds = wishlistRepository.findByMemberId(member.getId())
                .stream()
                .map(w -> w.getItem().getId())
                .toList();

        // 아이템 정보와 메인 이미지를 한번에 조회
        List<Item> items = itemDynamicSort.findItemsWithDSL(
                subCategoryName, skinIssue, priceSort);

//        if (StringUtils.hasText(skinIssue)) {
//            // 스킨이슈에 해당하는 제품 조회
//            items = itemRepository.findBySubCategoryNameAndSkinIssueWithMainImage(subCategoryName, skinIssue);
//        } else {
//            // 전체조회
//            items = itemRepository.findBySubCategoryNameWithMainImage(subCategoryName);
//        }

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
    public ApiResponse<ItemSearchResultDTO> searchByKeyword(Member member, String keyword) {

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
    개선된 검색 키워드에 맞는 아이템 조회(ElasticSearch 활용)
     */
    public ApiResponse<ItemSearchResultDTO> AdvancedSearchByKeyword(Member member, String keyword) throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("items")
                .query(q -> q
                        .bool(b -> b
                                // 상품명 정확 매칭 (최우선)
                                .should(sh -> sh
                                        .match(m -> m.field("name").query(keyword).boost(5.0f))
                                )

                                // 상품명 포함 (중간 우선순위)
                                .should(sh -> sh
                                        .wildcard(w -> w.field("name").value("*" + keyword + "*").boost(2.0f)))

                                // n-gram 방식 (각 글자별로)
                                .should(sh -> sh
                                        .matchPhrase(mp -> mp
                                                .field("name")
                                                .query(keyword)
                                                .slop(1)  // 단어 순서 바뀌어도 허용
                                                .boost(1.5f)))

                                // Fuzzy 검색 (오타 허용)
                                .should(sh -> sh
                                        .fuzzy(f -> f
                                                .field("name")
                                                .value(keyword)
                                                .fuzziness("2")
                                                .prefixLength(0)  // 접두사 길이 0
                                                .maxExpansions(50)
                                                .boost(1.0f)))
                        ))
                .sort(so -> so.score(sc -> sc.order(SortOrder.Desc))) // 점수 높은 순
        );

        // Elasticsearch 검색 실행 후 id만 뽑아내기
        SearchResponse<ItemDocument> response = searchElasticsearchClient.search(searchRequest, ItemDocument.class);
        List<Long> itemIds = response.hits().hits().stream()
                .map(hit -> hit.source().getId())
                .filter(Objects::nonNull)
                .toList();

        // 검색 결과가 없으면 빈 결과 반환
        if (itemIds.isEmpty()) {
            return ApiResponse.onSuccess(null); // 빈 DTO 반환
        }

        // 찜한 상품 Id
        List<WishList> wishLists = wishlistRepository.findByMemberId(member.getId());
        List<Long> wishListIds = wishLists.stream()
                .map(w -> w.getItem().getId())
                .toList();

        // 아이템 정보와 메인 이미지를 한번에 조회
        List<Item> items = itemRepository.findItemByItemIdsWithMainImage(itemIds);
        if (items.isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.ITEM_NOT_FOUND, null);
        }

        ItemSearchResultDTO itemSearchResultDTO = ItemSearchConverter.toItemSearchInfoDTO(items, wishListIds);
        return ApiResponse.onSuccess(itemSearchResultDTO);
    }


    /*
    비건 제품 조회
     */
    public ApiResponse<ItemSearchResultDTO> getVeganItems(
            Member member, String subCategory, String skinIssue, String priceSort) {

        // 찜한 상품 Id
        List<WishList> wishLists = wishlistRepository.findByMemberId(member.getId());
        List<Long> wishListIds = wishLists.stream()
                .map(w -> w.getItem().getId())
                .toList();

        // 아이템 정보와 메인 이미지를 한번에 조회
        List<Item> veganItems = itemDynamicSort.findVeganItemsWithDSL(
                subCategory, skinIssue, priceSort);

//        if (StringUtils.hasText(skinIssue)) {
//            veganItems = itemRepository.findByVeganItemsAndSkinIssueWithMainImage(subCategory, skinIssue);
//        } else {
//            veganItems = itemRepository.findByVeganTypeWithMainImage(subCategory);
//        }
//        if (veganItems.isEmpty()) {
//            return ApiResponse.onFailure(ErrorStatus.ITEM_NOT_FOUND, null);
//        }

        ItemSearchResultDTO itemSearchResultDTO = ItemSearchConverter.toItemSearchInfoDTO(
                veganItems, wishListIds);
        return ApiResponse.onSuccess(itemSearchResultDTO);
    }

}
