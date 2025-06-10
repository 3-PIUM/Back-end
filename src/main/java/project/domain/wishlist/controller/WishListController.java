package project.domain.wishlist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.domain.member.Member;
import project.domain.wishlist.dto.WishListResponse.WishListInfoDTO;
import project.domain.wishlist.dto.WishListResponse.WishListItemDTO;
import project.domain.wishlist.service.WishListService;
import project.global.response.ApiResponse;
import project.global.security.annotation.LoginMember;

@Tag(name = "찜 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishListController {

    private final WishListService wishListService;

    @Operation(
            summary = "찜 목록 조회",
            description = "로그인된 사용자가 찜한 아이템들을 조회합니다."
    )
    @GetMapping("/items")
    public ApiResponse<WishListInfoDTO> getWishList(
            @Parameter(hidden = true) @LoginMember Member member
    ) {
        return wishListService.getWishList(member.getId());
    }

    @Operation(
            summary = "찜 추가",
            description = "특정 아이템을 찜 목록에 추가합니다."
    )
    @PostMapping("/{itemId}")
    public ApiResponse<WishListItemDTO> addWishList(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "추가할 아이템 ID") @PathVariable Long itemId
    ) {
        WishListItemDTO addedWishList = wishListService.addWishList(member, itemId);
        return ApiResponse.onSuccess("추가된 아이템", addedWishList);
    }

    @Operation(
            summary = "찜 취소",
            description = "찜 되어 있는 아이템을 찜 취소합니다."
    )
    @DeleteMapping("/{wishListId}")
    public ApiResponse<WishListItemDTO> deleteWishList(
            @Parameter(description = "취소할 찜 ID") @PathVariable Long wishListId
    ) {
        WishListItemDTO deletedWishlist = wishListService.deleteWishlist(wishListId);
        return ApiResponse.onSuccess("삭제된 아이템", deletedWishlist);
    }
}
