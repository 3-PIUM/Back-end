package project.domain.cart.controller;

import com.google.zxing.WriterException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.domain.cart.dto.CartRequest;
import project.domain.cart.dto.CartResponse.CartDTO;
import project.domain.cart.service.CartService;
import project.domain.member.Member;
import project.global.response.ApiResponse;
import project.global.security.annotation.LoginMember;

import java.io.IOException;

import static project.domain.cart.dto.CartRequest.*;

@Tag(name = "장바구니 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @Operation(
            summary = "장바구니 아이템 조회",
            description = "로그인된 사용자의 장바구니 아이템 목록을 조회합니다."
    )
    @GetMapping("/items")
    public ApiResponse<CartDTO> getCartItems(@Parameter(hidden = true) @LoginMember Member member) {
        return cartService.getCartItems(member.getId());
    }


    @Operation(
            summary = "장바구니에 아이템 추가",
            description = "특정 상품을 장바구니에 추가합니다."
    )
    @PostMapping("/items/{itemId}")
    public ApiResponse<Boolean> addItemToCart(@Parameter(hidden = true) @LoginMember Member member,
                                              @Parameter(description = "추가할 아이템의 ID") @PathVariable Long itemId,
                                              @Parameter(description = "추가할 수량") @RequestBody AddItemDTO addItemDTO) {
        return cartService.addItemToCart(member.getId(), itemId, addItemDTO.getQuantity());
    }

    @Operation(
            summary = "아이템 수량 증가(+1)",
            description = "장바구니에 담긴 특정 상품의 수량을 1 증가시킵니다."
    )
    @PatchMapping("/items/{itemId}/increase")
    public ApiResponse<Boolean> increaseCartItemQuantity(@Parameter(hidden = true) @LoginMember Member member,
                                                         @Parameter(description = "수량 추가시킬 아이템 ID") @PathVariable Long itemId) {
        return cartService.updateCartItem(member.getId(), itemId, 1);
    }

    @Operation(
            summary = "아이템 수량 감소(-1)",
            description = "장바구니에 담긴 특정 상품의 수량을 1 감소시킵니다."
    )
    @PatchMapping("/items/{itemId}/decrease")
    public ApiResponse<Boolean> decreaseCartItemQuantity(@Parameter(hidden = true) @LoginMember Member member,
                                                         @Parameter(description = "수량 감소시킬 아이템 ID") @PathVariable Long itemId) {
        return cartService.updateCartItem(member.getId(), itemId, -1);
    }


    @Operation(
            summary = "아이템 삭제",
            description = "특정 아이템을 장바구니에서 삭제합니다."
    )
    @DeleteMapping("/items/{itemId}")
    public ApiResponse<Boolean> removeCartItem(@Parameter(hidden = true) @LoginMember Member member,
                                               @Parameter(description = "삭제할 아이템 ID") @PathVariable Long itemId) {
        return cartService.removeCartItem(member.getId(), itemId);
    }

    @Operation(
            summary = "QR 생성",
            description = "결제창으로 넘어가는 QR코드 이미지를 생성해줍니다."
    )
    @PostMapping("/qr")
    public ApiResponse<byte[]> getQrCode(@LoginMember Member member) throws IOException, WriterException {
        /*
        generateQrcode에서 url 수정만 하면 됨
         */
//        return cartService.generateQrCode(member.getId());
        return cartService.generateQrCode(1L);
    }
}
