package project.domain.wishlist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.item.Item;
import project.domain.item.repository.ItemRepository;
import project.domain.itemimage.ItemImage;
import project.domain.itemimage.enums.ImageType;
import project.domain.itemimage.repository.ItemImageRepository;
import project.domain.member.Member;
import project.domain.wishlist.WishList;
import project.domain.wishlist.dto.WishListConverter;
import project.domain.wishlist.dto.WishListResponse.DeleteItemDTO;
import project.domain.wishlist.dto.WishListResponse.WishListResponseDTO;
import project.domain.wishlist.repository.WishlistRepository;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishListService {

    private final WishlistRepository wishlistRepository;
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;

    /*
        찜 목록 조회
         */
    public ApiResponse<List<WishListResponseDTO>> getWishList(Long memberId) {
        List<WishList> wishLists = wishlistRepository.findByMemberId(memberId);

        List<Long> itemIds = wishLists.stream()
                .map(w -> w.getItem().getId())
                .distinct()
                .toList();

        // 각 아이템들에 대한 메인 이미지 저장
        List<ItemImage> mainImages = itemImageRepository.findByItemIdInAndImageType(itemIds, ImageType.MAIN);

        Map<Long, ItemImage> itemImageMap = mainImages.stream()
                .collect(Collectors.toMap(
                        itemImage -> itemImage.getItem().getId()
                        , Function.identity()
                ));

        return ApiResponse.onSuccess(WishListConverter.toWishListResponseDTOList(wishLists, itemImageMap));
    }

    /*
    찜 등록
     */
    @Transactional
    public ApiResponse<WishListResponseDTO> addWishList(Member member, Long itemId) {
        // item 정보 확인
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));

        // 찜 목록에 등록되어 있는지 체크
        Optional<WishList> existingWishList = wishlistRepository.findByMemberIdAndItemId(member.getId(), itemId);

        if (existingWishList.isPresent()) {
            // 찜 목록에 있는 경우 등록 X
            throw new GeneralException(ErrorStatus.WISHLIST_ITEM_EXIST);
        } else {
            WishList newWishList = WishList.createWishList(member, item);
            wishlistRepository.save(newWishList);

            List<ItemImage> mainImages = itemImageRepository.findByItemIdAndImageType(item.getId(), ImageType.MAIN);
            ItemImage itemImage = mainImages != null ? mainImages.get(0) : null;


            return ApiResponse.onSuccess("찜 목록에 추가된 아이템",
                    WishListConverter.toWishListResponseDTO(newWishList, itemImage));
        }
    }

    /*
    찜 취소
     */
    @Transactional
    public ApiResponse<DeleteItemDTO> deleteWishlist(Member member, Long itemId) {
        WishList deleteWishList = findWishList(member.getId(), itemId);

        wishlistRepository.deleteById(deleteWishList.getId());

        Item deletedItem = deleteWishList.getItem();
        return ApiResponse.onSuccess("삭제된 아이템", WishListConverter.toDeleteItemDTO(deletedItem));
    }

    /*
    찜 목록 찾기
     */
    private WishList findWishList(Long memberId, Long itemId) {
        return wishlistRepository.findByMemberIdAndItemId(memberId, itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WISHLIST_NOT_FOUND));
    }
}
