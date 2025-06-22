package project.domain.item.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.domain.item.dto.ItemSearchResponse.ItemSearchInfoDTO;
import project.domain.item.dto.ItemSearchResponse.ItemSearchResultDTO;
import project.domain.item.service.ItemSearchService;
import project.domain.member.Member;
import project.global.response.ApiResponse;
import project.global.security.annotation.LoginMember;

import java.util.List;


@Tag(name = "상품 검색 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemSearchController {

    private final ItemSearchService itemSearchService;

    @Operation(
            summary = "서브 카테고리 아이템 조회",
            description = "입력한 서브 카테고리에 해당하는 아이템들을 500개씩 조회합니다."
    )
    @GetMapping("/list/{subCategory}")
    public ApiResponse<ItemSearchResultDTO> getItemsBySubCategory(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "서브 카테고리명") @PathVariable String subCategory,
            @Parameter(description = "피부 고민") @RequestParam(defaultValue = "") String skinIssue
    ) {
        return itemSearchService.getItemsBySubCategory(subCategory, member, skinIssue);
    }

    @Operation(
            summary = "검색 키워드 관련 아이템 조회",
            description = "검색 키워드와 유사한 아이템 정보를 20개씩 조회합니다."
    )
    @GetMapping("/search/list/{keyword}")
    public ApiResponse<ItemSearchResultDTO> getItemsByKeyword(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "검색 키워드") @PathVariable String keyword
    ) {
        return itemSearchService.getItemsByKeyword(member, keyword);
    }

    @Operation(
            summary = "(비건) 서브 카테고리 아이템 조회",
            description = "(비건) 서브 카테고리 아이템 조회"
    )
    @GetMapping("/vegan/list/{subCategory}")
    public ApiResponse<ItemSearchResultDTO> getVeganItems(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "서브 카테고리명") @PathVariable String subCategory,
            @Parameter(description = "피부 고민") @RequestParam(defaultValue = "") String skinIssue
    ) {

        return itemSearchService.getVeganItems(member, subCategory, skinIssue);
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
