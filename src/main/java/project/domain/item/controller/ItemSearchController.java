package project.domain.item.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.domain.item.dto.ItemSearchResponse.ItemSearchInfoDTO;
import project.domain.item.dto.ItemSearchResponse.ItemSearchResultDTO;
import project.domain.item.dto.ItemSearchResponse.Top10ItemsInfoDTO;
import project.domain.item.service.ItemSearchService;
import project.domain.member.Member;
import project.domain.popularitem.dto.PopularItemDTO;
import project.global.redis.service.ItemViewRedis;
import project.global.response.ApiResponse;
import project.global.security.annotation.LoginMember;

import java.io.IOException;
import java.util.List;


@Tag(name = "상품 검색 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemSearchController {

    private final ItemSearchService itemSearchService;
    private final ItemViewRedis itemViewRedis;

    @Operation(
            summary = "현재 인기 있는 상품 TOP 10 조회",
            description = "1시간 간격으로 업데이트 됩니다."
    )
    @GetMapping("/top10")
    public ApiResponse<List<Top10ItemsInfoDTO>> getTop10Items() {
        List<PopularItemDTO> top10PopularItems = itemViewRedis.getTop10PopularItems();
        return itemSearchService.getTop10Items(top10PopularItems);
    }


    @Operation(
            summary = "서브 카테고리 아이템 조회",
            description = "입력한 서브 카테고리에 해당하는 아이템들을 조회합니다."
    )
    @GetMapping("/list/{subCategory}")
    public ApiResponse<ItemSearchResultDTO> getItemsBySubCategory(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "서브 카테고리명") @PathVariable String subCategory,
            @Parameter(description = "피부 고민") @RequestParam(defaultValue = "") String skinIssue,
            @Parameter(description = "정렬 타입") @RequestParam(defaultValue = "") String priceSort
    ) {
        return itemSearchService.getItemsBySubCategory(subCategory, member, skinIssue, priceSort);
    }

    @Operation(
            summary = "검색 키워드 관련 아이템 조회",
            description = "검색 키워드와 유사한 아이템 정보를 조회합니다."
    )
    @GetMapping("/search/list/{keyword}")
    public ApiResponse<ItemSearchResultDTO> searchByKeyword(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "검색 키워드") @PathVariable String keyword
    ) {
        return itemSearchService.searchByKeyword(member, keyword);
    }

    @Operation(
            summary = "(향상된 버전) 검색 키워드 관련 아이템 조회",
            description = "검색 키워드와 유사한 아이템 정보를 조회합니다."
    )
    @GetMapping("/advancedSearch/list/{keyword}")
    public ApiResponse<ItemSearchResultDTO> advancedSearchByKeyword(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "검색 키워드") @PathVariable String keyword
    ) throws IOException {
        return itemSearchService.AdvancedSearchByKeyword(member, keyword);
    }

    @Operation(
            summary = "(비건) 서브 카테고리 아이템 조회",
            description = "(비건) 서브 카테고리 아이템 조회"
    )
    @GetMapping("/vegan/list/{subCategory}")
    public ApiResponse<ItemSearchResultDTO> getVeganItems(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "서브 카테고리명") @PathVariable String subCategory,
            @Parameter(description = "피부 고민") @RequestParam(defaultValue = "") String skinIssue,
            @Parameter(description = "정렬타입") @RequestParam(defaultValue = "") String priceSort
    ) {

        return itemSearchService.getVeganItems(member, subCategory, skinIssue, priceSort);
    }

    @Operation(
            summary = "카테고리 추천 아이템 조회",
            description = "카테고리별 추천 아이템 조회"
    )
    @GetMapping("/list")
    public ApiResponse<List<ItemSearchInfoDTO>> getItemByCategory(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "카테고리명") @RequestParam(required = false) String category
    ) {
        return itemSearchService.getItemsByCategoryOrderByCount(member, category);
    }

}
