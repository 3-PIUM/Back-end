package project.domain.cart.controller;

import com.google.zxing.WriterException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.domain.cart.dto.CartResponse;
import project.domain.cart.dto.CartResponse.CartDTO;
import project.domain.cart.dto.CartResponse.CartItemDTO;
import project.domain.cart.dto.CartResponse.SummaryCartItemDTO;
import project.domain.cart.service.CartService;
import project.domain.member.Member;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;
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
    public ApiResponse<CartItemDTO> addItemToCart(@Parameter(hidden = true) @LoginMember Member member,
                                                  @Parameter(description = "추가할 아이템의 ID") @PathVariable Long itemId,
                                                  @Parameter(description = "추가할 수량") @RequestBody AddItemDTO addItemDTO) {
        return cartService.addItemToCart(member.getId(), itemId, addItemDTO);
    }

    @Operation(

            summary = "장바구니 아이템 수량 증가(+1)",
            description = "장바구니에 담긴 특정 상품의 수량을 1 증가시킵니다."
    )
    @PatchMapping("/items/{cartItemId}/increase")
    public ApiResponse<Void> increaseCartItemQuantity(@Parameter(hidden = true) @LoginMember Member member,
                                                      @Parameter(description = "수량 추가시킬 CartItem ID") @PathVariable Long cartItemId) {
        return cartService.updateCartItem(member.getId(), cartItemId, 1);
    }

    @Operation(
            summary = "장바구니 아이템 수량 감소(-1)",
            description = "장바구니에 담긴 특정 상품의 수량을 1 감소시킵니다."
    )
    @PatchMapping("/items/{cartItemId}/decrease")
    public ApiResponse<Void> decreaseCartItemQuantity(@Parameter(hidden = true) @LoginMember Member member,
                                                      @Parameter(description = "수량 감소시킬 CartItem ID") @PathVariable Long cartItemId) {
        return cartService.updateCartItem(member.getId(), cartItemId, -1);
    }

    @Operation(
            summary = "장바구니 아이템 옵션 변경",
            description = "장바구니에 담긴 특정 상품의 옵션을 변경합니다."
    )
    @PatchMapping("/items/{cartItemId}/updateOption")
    public ApiResponse<Void> updateCartItemOption(@Parameter(hidden = true) @LoginMember Member member,
                                                  @Parameter(description = "옵션 변경할 CartItem ID") @PathVariable Long cartItemId,
                                                  @Parameter(description = "변경 옵션명") @RequestBody UpdateOptionDTO option) {
        return cartService.updateCartItemOption(cartItemId, option.getChangeOption());
    }


    @Operation(
            summary = "장바구니 아이템 삭제",
            description = "특정 아이템을 장바구니에서 삭제합니다."
    )
    @DeleteMapping("/items/{cartItemId}")
    public ApiResponse<SummaryCartItemDTO> removeCartItem(@Parameter(hidden = true) @LoginMember Member member,
                                                          @Parameter(description = "삭제할 CartItem ID") @PathVariable Long cartItemId) {
        return cartService.removeCartItem(member.getId(), cartItemId);
    }

    @Operation(
            summary = "장바구니 초기화",
            description = "장바구니를 초기화합니다."
    )
    @DeleteMapping("/")
    public ApiResponse<Void> removeCartItem(@Parameter(hidden = true) @LoginMember Member member) {
        return cartService.clearCart(member.getId());
    }

    @Operation(
            summary = "결제 QR 생성",
            description = "결제창으로 넘어가는 QR코드 이미지를 생성해줍니다."
    )
    @PostMapping("/qr")
    public ApiResponse<byte[]> getQrCode(@LoginMember Member member) throws IOException, WriterException {
        /*
        generateQrcode에서 url 수정만 하면 됨
         */
        return cartService.generateQrCode(member.getId());
    }
}
