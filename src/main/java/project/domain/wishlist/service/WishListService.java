package project.domain.wishlist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.item.Item;
import project.domain.item.repository.ItemRepository;
import project.domain.member.Member;
import project.domain.wishlist.WishList;
import project.domain.wishlist.dto.WishListConverter;
import project.domain.wishlist.dto.WishListResponse.WishListInfoDTO;
import project.domain.wishlist.dto.WishListResponse.WishListItemDTO;
import project.domain.wishlist.repository.WishlistRepository;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishListService {

    private final WishlistRepository wishlistRepository;
    private final ItemRepository itemRepository;

    /*
        찜 목록 조회
         */
    public ApiResponse<WishListInfoDTO> getWishList(Long memberId) {
        List<WishList> wishList = wishlistRepository.findByMemberId(memberId);
        WishListInfoDTO wishListDTO = WishListConverter.toWishListDTO(wishList);
        
        return ApiResponse.onSuccess(wishListDTO);
    }

    /*
    찜 등록
     */
    @Transactional
    public WishListItemDTO addWishList(Member member, Long itemId) {
        // item 정보 확인
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));

        // 찜 목록에 등록되어 있는지 체크
        Optional<WishList> existingWishList = wishlistRepository.findByItemId(itemId);

        if (existingWishList.isPresent()) {
            // 찜 목록에 있는 경우 등록 X
            throw new GeneralException(ErrorStatus.WISHLIST_ITEM_EXIST);
        }else {
            WishList newWishList = WishList.createWishList(member, item);
            wishlistRepository.save(newWishList);

            return WishListConverter.toWishListItemDTO(newWishList.getItem());
        }
    }
    
    /*
    찜 취소
     */
    @Transactional
    public WishListItemDTO deleteWishlist(Long wishlistId) {
        WishList deleteWishList = findWishList(wishlistId);

        wishlistRepository.deleteById(wishlistId);

        return WishListConverter.toWishListItemDTO(deleteWishList.getItem());
    }

    /*
    찜 목록 찾기
     */
    private WishList findWishList(Long wishlistId) {
        return wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WISHLIST_NOT_FOUND));
    }
}
