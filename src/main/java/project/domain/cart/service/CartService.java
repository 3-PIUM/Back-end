package project.domain.cart.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.cart.Cart;
import project.domain.cart.dto.CartConverter;
import project.domain.cart.dto.CartRequest.AddItemDTO;
import project.domain.cart.dto.CartResponse.CartDTO;
import project.domain.cart.dto.CartResponse.CartItemDTO;
import project.domain.cart.dto.CartResponse.SummaryCartItemDTO;
import project.domain.cart.repository.CartRepository;
import project.domain.cartitem.CartItem;
import project.domain.cartitem.repository.CartItemRepository;
import project.domain.item.Item;
import project.domain.item.repository.ItemRepository;
import project.domain.item.service.ExchangeRateService;
import project.domain.itemimage.ItemImage;
import project.domain.itemimage.enums.ImageType;
import project.domain.itemimage.repository.ItemImageRepository;
import project.domain.member.Member;
import project.domain.member.repository.MemberRepository;
import project.domain.purchasehistory.PurchaseHistory;
import project.domain.purchasehistory.repository.PurchaseHistoryRepository;
import project.global.kafka.dto.cart.CartEventDTO;
import project.global.kafka.dto.purchase.PurchaseEventDTO;
import project.global.kafka.service.cart.CartLogProducer;
import project.global.kafka.service.purchase.PurchaseLogProducer;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;
import project.global.util.ImageUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ItemImageRepository itemImageRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final PurchaseLogProducer purchaseLogProducer;
    private final CartLogProducer cartLogProducer;
    private final ExchangeRateService exchangeRateService;

    private double changeToRate(String lang) {
        if ("KR".equalsIgnoreCase(lang)) {
            return 1.0;
        } else {
            return exchangeRateService.getRate(lang.toUpperCase());
        }
    }

    /*
    장바구니 아이템 상세 조회
     */
    public ApiResponse<CartDTO> getCartDetailItems(String cartId, String lang) {
        List<Long> cartIdList = Arrays.stream(cartId.split(","))
            .map(Long::parseLong)
            .toList();

        List<CartItem> allById = cartItemRepository.findAllById(cartIdList);

        // 장바구니에 담긴 아이템 id 저장
        List<Long> ids = allById.stream()
            .map(cartItem -> cartItem.getItem().getId())
            .distinct()
            .toList();

        // 각 아이템 별 메인 이미지 저장
        List<ItemImage> mainImages = itemImageRepository.findByItemIdInAndImageType(ids,
            ImageType.MAIN);

        Map<Long, ItemImage> itemImageMap = mainImages.stream()
            .collect(Collectors.toMap(
                itemImage -> itemImage.getItem().getId()
                , Function.identity()
            ));

        double rate = changeToRate(lang);
        CartDTO cartDTO = CartConverter.toCartDTO(allById.get(0).getCart(), allById, itemImageMap,
            lang, rate);
        return ApiResponse.onSuccess(cartDTO);
    }

    /*
    장바구니 아이템 조회
     */
    public ApiResponse<CartDTO> getCartItems(Long memberId, String lang) {
        Cart cart = findCartByMember(memberId);
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        // 장바구니에 담긴 아이템 id 저장
        List<Long> ids = cartItems.stream()
            .map(cartItem -> cartItem.getItem().getId())
            .distinct()
            .toList();

        // 각 아이템 별 메인 이미지 저장
        List<ItemImage> mainImages = itemImageRepository.findByItemIdInAndImageType(ids,
            ImageType.MAIN);

        Map<Long, ItemImage> itemImageMap = mainImages.stream()
            .collect(Collectors.toMap(
                itemImage -> itemImage.getItem().getId()
                , Function.identity()
            ));

        double rate = changeToRate(lang);
        CartDTO cartDTO = CartConverter.toCartDTO(cart, cartItems, itemImageMap, lang, rate);
        return ApiResponse.onSuccess(cartDTO);
    }

    /*
    장바구니 추가
     */
    @Transactional
    public ApiResponse<CartItemDTO> addItemToCart(Member member, Long itemId, AddItemDTO addItemDTO,
        String lang) {
        Cart cart = findCartByMember(member.getId()); // 카트 정보 조회
        Item addItem = findItemById(itemId); // 추가할 아이템 정보 조회

        // 카트에 이미 존재하는지 확인
        // 존재O -> 수량 추가, 존재X -> 카트에 새로 등록
        Optional<CartItem> existingCartItem = cartItemRepository.findFirstByCartIdAndItemIdAndItemOption(
            cart.getId(), addItem.getId(), addItemDTO.getItemOption());
        CartItem cartItem;
        if (existingCartItem.isPresent()) { // 수량 추가
            cartItem = existingCartItem.get();
            cartItem.updateQuantity(addItemDTO.getQuantity());
        } else {    // 카트에 아이템 추가
            cartItem = CartItem.createCartItem(cart, addItem, addItemDTO);
        }
        cartItemRepository.save(cartItem);

        // 총액 업데이트
        updateCartTotalPrice(cart);

        List<ItemImage> itemImages = itemImageRepository.findByItemIdAndImageType(itemId,
            ImageType.MAIN);
        ItemImage mainImage = itemImages != null ? itemImages.get(0) : null;

        // 장바구니 로그 전송
        CartEventDTO cartEventDTO = CartEventDTO.builder()
            .memberId(member.getId())
            .birth(member.getBirth())
            .gender(member.getGender())
            .area(member.getArea())
            .skinType(member.getSkinType())
            .skinIssues(member.getSkinIssue())
            .personalType(member.getPersonalType())
            .itemId(itemId)
            .eventTime(System.currentTimeMillis())
            .build();

        cartLogProducer.sendCartLog(cartEventDTO);

        double rate = changeToRate(lang);
        CartItemDTO cartItemDTO = CartConverter.toCartItemDTO(cartItem, mainImage, lang, rate);
        return ApiResponse.onSuccess("장바구니에 추가된 아이템", cartItemDTO);
    }

    /*
    장바구니 아이템 수정(수량 변동)
     */
    @Transactional
    public ApiResponse<Void> updateCartItem(Long memberId, Long cartItemId,
        Integer changeQuantity) {
        Cart cart = findCartByMember(memberId); // 카트 정보 조회

        // 카트에 아이템 존재하는지 체크
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.CART_ITEM_NOT_FOUND));

        // 수량 업데이트
        int updatedQuantity = cartItem.updateQuantity(changeQuantity);
        if (updatedQuantity <= 0) {
            cartItemRepository.delete(cartItem);
        }

        // 총액 업데이트
        updateCartTotalPrice(cart);

        return ApiResponse.OK;
    }

    /*
    장바구니 아이템 수정(옵션)
     */
    @Transactional
    public ApiResponse<Void> updateCartItemOption(Long cartItemId, String changeOption) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.CART_ITEM_NOT_FOUND));

        cartItem.updateOption(changeOption);

        // 같은 옵션인 아이템이 이미 장바구니에 존재하는 경우 두개를 하나로 합침
        List<CartItem> checkCartItem = cartItemRepository.findByCartIdAndItemIdAndItemOption(
            cartItem.getCart().getId(), cartItem.getItem().getId(), changeOption);
        if (checkCartItem.size() == 2) {
            checkCartItem.get(1).updateQuantity(cartItem.getQuantity());
            cartItemRepository.delete(cartItem);
        }

        return ApiResponse.OK;
    }

    /*
    장바구니 아이템 삭제
     */
    @Transactional
    public ApiResponse<SummaryCartItemDTO> removeCartItem(Long memberId, Long cartItemId,
        String lang) {
        Cart cart = findCartByMember(memberId); // 카트 정보 조회

        // 카트에 해당 아이템이 존재하는지 체크
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.CART_ITEM_NOT_FOUND));

        // 아이템 삭제
        cartItemRepository.delete(cartItem);

        // 총액 업데이트
        updateCartTotalPrice(cart);

        double rate = changeToRate(lang);
        SummaryCartItemDTO cartItemDTO = CartConverter.toSummaryCartItemDTO(cartItem, lang, rate);
        return ApiResponse.onSuccess("장바구니에서 삭제된 아이템", cartItemDTO);
    }

    /*
    장바구니 초기화
     */
    @Transactional
    public ApiResponse<Void> clearCart(Long memberId) {
        Cart cart = findCartByMember(memberId);
        cart.clearCart();

        return ApiResponse.OK;
    }


    /**
     * QR 코드 생성 메소드
     */
    @Transactional
    public ApiResponse<byte[]> generateQrCode(Long memberId, String cartItemIds)
        throws WriterException, IOException {
        // 존재하는 멤버인지 id로 체크
        memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND_BY_ID));

        // QRCodeWriter(QR 생성기) 객체 생성
        QRCodeWriter writer = new QRCodeWriter();

        // url 생성
        String baseUrl = String.format("https://pium-front.vercel.app/cart/pay/%d", memberId);
        String qrUrl = baseUrl + "?" + "cartItemIds" + "=" + cartItemIds;

        // `URL`을 QR 코드 형식의 비트 매트릭스로 인코딩
        BitMatrix bitMatrix = writer.encode(qrUrl, BarcodeFormat.QR_CODE, 200, 200);

        // Byte 매트릭스->이미지 변환하기 위한 출력 스트림 생성
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Byte 매트릭스를 PNG 형식의 이미지로 출력 스트림에 작성
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        // 출력 스트림의 내용을 바이트 배열로 반환
        return ApiResponse.onSuccess(outputStream.toByteArray());
    }

    /*
    결제
     */
    @Transactional
    public ApiResponse<Void> pay(Long memberId, String cartItemIds, String lang) {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND_BY_ID));

        // 결제할 상품 카트 id 문자열 -> 리스트로 변환
        List<Long> ids = Arrays.stream(cartItemIds.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(Long::valueOf)
            .toList();

        // 결제할 아이템만 장바구니에서 필터링
        List<CartItem> cartitems = cartItemRepository.findByCartMemberId(memberId);
        List<CartItem> payItems = cartitems.stream()
            .filter(cartItem -> ids.contains(cartItem.getId()))
            .toList();

        // 결제할 아이템 이미지 찾기
        List<Long> itemIds = payItems.stream()
            .map(pi -> pi.getItem().getId())
            .toList();
        List<ItemImage> mainImages = itemImageRepository.findByItemIdInAndImageType(itemIds,
            ImageType.MAIN);
        Map<Long, ItemImage> itemImageMap = mainImages.stream()
            .collect(Collectors.toMap(
                mi -> mi.getItem().getId()
                , Function.identity()));

        // 구매내역 등록
        payItems.forEach(payItem -> {
            PurchaseHistory newPH = PurchaseHistory.builder()
                .member(member)
                .itemId(payItem.getItem().getId())
                .itemName(payItem.getItem().getName(lang))
                .price(payItem.getItem().getSalePrice())
                .quantity(payItem.getQuantity())
                .discountRate(payItem.getItem().getDiscountRate())
                .imgUrl(ImageUtil.getMainImageUrl(payItem.getItem().getId(), itemImageMap))
                .build();

            purchaseHistoryRepository.save(newPH);
        });

        // 장바구니에서 구매된 제품 삭제
        cartItemRepository.deleteAll(payItems);
        // 장바구니 총 금액 업데이트
        Cart cart = findCartByMember(memberId);
        updateCartTotalPrice(cart);

        // 구매 완료후 구매 이벤트 발행
        PurchaseEventDTO eventDTO = PurchaseEventDTO.builder()
            .memberId(memberId)
            .birth(member.getBirth())
            .gender(member.getGender())
            .area(member.getArea())
            .personalType(member.getPersonalType())
            .skinType(member.getSkinType())
            .skinIssues(member.getSkinIssue())
            .cartItemIds(cartitems.stream()
                .map(ci -> ci.getItem().getId())
                .toList())
            .purchaseItemIds(itemIds)
            .eventTime(System.currentTimeMillis())
            .build();

        // 메세지 발행
        purchaseLogProducer.sendPurchaseLog(eventDTO);

        return ApiResponse.OK;
    }


    /*
    멤버Id로 카트 정보 조회 메소드
     */
    private Cart findCartByMember(Long memberId) {
        return cartRepository.findByMemberId(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.CART_NOT_FOUND));
    }

    // 아이템Id로 상품 정보 조회 메소드
    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));
    }

    /*
    총 금액 업데이트 메소드
     */
    private void updateCartTotalPrice(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        int totalPrice = cartItems.stream()
            .mapToInt(cartItem -> cartItem.getItem().getSalePrice() * cartItem.getQuantity())
            .sum();
        cart.updateTotalPrice(totalPrice);
        cartRepository.save(cart);
    }
}
