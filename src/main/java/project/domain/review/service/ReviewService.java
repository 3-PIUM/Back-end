package project.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.domain.item.Item;
import project.domain.item.repository.ItemRepository;
import project.domain.makeupreviewoption.MakeupReviewOption;
import project.domain.makeupreviewoptionlist.MakeupReviewOptionList;
import project.domain.makeupreviewoptionlist.repository.MakeupReviewOptionListRepository;
import project.domain.member.Member;
import project.domain.member.repository.MemberRepository;
import project.domain.review.Review;
import project.domain.review.dto.ReviewConverter;
import project.domain.review.dto.ReviewRequest.AddReviewBodyDTO;
import project.domain.review.dto.ReviewRequest.AddReviewDTO;
import project.domain.review.dto.ReviewRequest.EditReviewBodyDTO;
import project.domain.review.dto.ReviewRequest.ImageBodyDTO;
import project.domain.review.dto.ReviewResponse.ReviewDTO;
import project.domain.review.dto.ReviewResponse.ReviewListDTO;
import project.domain.review.dto.ReviewResponse.ReviewOptionListDTO;
import project.domain.review.dto.ReviewResponse.SelectOptionDTO;
import project.domain.review.repository.ReviewRepository;
import project.domain.reviewimage.ReviewImage;
import project.domain.reviewimage.repository.ReviewImageRepository;
import project.domain.reviewoption.ReviewOption;
import project.domain.reviewoption.repository.ReviewOptionRepository;
import project.domain.reviewrecommendstatus.ReviewRecommendStatus;
import project.domain.reviewrecommendstatus.repository.ReviewRecommendStatusRepository;
import project.domain.selectoption.SelectOption;
import project.domain.selectoption.repository.SelectOptionRepository;
import project.domain.subcategory.SubCategory;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;
import project.global.s3.util.S3Uploader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final S3Uploader s3Uploader;
    private final SelectOptionRepository selectOptionRepository;
    private final ReviewOptionRepository reviewOptionRepository;
    private final MakeupReviewOptionListRepository makeupReviewOptionListRepository;
    private final ReviewRecommendStatusRepository reviewRecommendStatusRepository;

    /*
    리뷰 옵션 조회
     */
    public ApiResponse<ReviewOptionListDTO> getReviewOption(Long itemId) {
        Item item = isExistsItem(itemId);
        SubCategory subCategory = item.getSubCategory();

        // 메이크업 제품인지 확인후 옵션 별도 처리
        Set<String> makeupCategory = Set.of("베이스메이크업", "립메이크업", "아이메이크업");
        if (makeupCategory.contains(subCategory.getName())) {
            List<MakeupReviewOptionList> optionLists = makeupReviewOptionListRepository.findByItemIdWithReviewOptions(itemId);
            List<MakeupReviewOption> options = optionLists.stream()
                    .map(MakeupReviewOptionList::getMakeupReviewOption).toList();

            ReviewOptionListDTO makeupReviewOptionListDTO = ReviewConverter.toMakeupReviewOptionListDTO(item, options);
            return ApiResponse.onSuccess(makeupReviewOptionListDTO);
        } else {
            List<ReviewOption> options = reviewOptionRepository.findBySubCategoryId(subCategory.getId());
            ReviewOptionListDTO reviewOptionListDTO = ReviewConverter.toReviewOptionListDTO(item, options);
            return ApiResponse.onSuccess(reviewOptionListDTO);
        }
    }

    /*
        리뷰 조회
         */
    public ApiResponse<ReviewListDTO> getReview(Member member, Long itemId) {
        isExistsItem(itemId);

        List<Review> reviewList = reviewRepository.findByItemId(itemId);
        // 로그인 유저가 추천한 리뷰 조회
        List<Review> recommendedReviewList = new ArrayList<>();
        List<Long> recommendedIds = new ArrayList<>();
        if (member != null) {
            recommendedReviewList = reviewRepository.findBMemberRecommendedWithReviewStatus(itemId, member.getId());
            recommendedIds = recommendedReviewList.stream()
                    .map(Review::getId)
                    .distinct()
                    .toList();
        }

        List<Long> reviewIds = reviewList.stream()
                .map(Review::getId)
                .toList();

        // 리뷰 선택 옵션
        List<SelectOption> allSelectOptions = selectOptionRepository.findByReviewIdIn(reviewIds);
        Map<Long, List<SelectOption>> selectOptions = allSelectOptions.stream()
                .collect(Collectors.groupingBy(
                        selectOption -> selectOption.getReview().getId()
                ));

        ReviewListDTO reviewListDTO = ReviewConverter.toReviewListDTO(reviewList, selectOptions, recommendedIds);
        return ApiResponse.onSuccess(reviewListDTO);
    }

    /*
    리뷰 등록
     */
    @Transactional
    public ApiResponse<ReviewDTO> addReview(Long memberId, Long itemId, AddReviewBodyDTO reviewData) {
        Member member = isExistsMember(memberId);
        Item item = isExistsItem(itemId);

        try {
            List<MultipartFile> files = reviewData.getFiles();
            List<String> urls = new ArrayList<>();

            // s3에 리뷰 이미지 저장 후 해당 url 반환
            if (files != null) {
                urls = s3Uploader.uploadFiles(files, "review-images");
            }

            AddReviewDTO addReviewDTO = ReviewConverter
                    .toAddReviewDTO(
                            memberId, itemId, reviewData.getContent(), reviewData.getRating(), urls);

            // 리뷰 생성
            Review newReview = Review.createReview(
                    member, item, addReviewDTO.getContent(), addReviewDTO.getRating(), addReviewDTO.getReviewImages());
            reviewRepository.save(newReview);

            // 선택한 리뷰 옵션 테이블 리스트 생성
            List<SelectOption> selectOptions = new ArrayList<>();
            List<SelectOptionDTO> reviewOption = reviewData.getSelectOptions();
            if (reviewOption != null && !reviewOption.isEmpty()) {
                reviewOption.forEach(r -> {
                    SelectOption selectOption = SelectOption.builder()
                            .review(newReview)
                            .name(r.getName())
                            .selection(r.getSelectOption())
                            .build();
                    selectOptions.add(selectOption);
                });

                selectOptionRepository.saveAll(selectOptions);
            }

            ReviewDTO reviewDTO = ReviewConverter.toReviewDTO(newReview, selectOptions, new ArrayList<>());
            return ApiResponse.onSuccess(reviewDTO);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    /*
    리뷰 수정
     */
    @Transactional
    public ApiResponse<Void> editReview(Long reviewId, EditReviewBodyDTO editReviewData, List<MultipartFile> newImages) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_NOT_FOUND));

        List<ImageBodyDTO> imageUrls = editReviewData.getReviewImages();
        if (imageUrls != null && newImages != null) {
            // 새로운 이미지 넣기
            setNewImages(imageUrls, newImages);
            // 새로 추가한 이미지만 S3에 저장
            updateReviewImages(imageUrls);
            // 더이상 필요없는 이미지 파일 S3에서 삭제
            deleteOriginalImages(reviewId, imageUrls);
        }


        // 리뷰 옵션 설정
        List<SelectOption> reviewOptions = new ArrayList<>();
        List<SelectOptionDTO> selectOptions = editReviewData.getSelectOptions();
        if (selectOptions != null && !selectOptions.isEmpty()) {
            for (SelectOptionDTO s : selectOptions) {
                reviewOptions.add(SelectOption.builder()
                        .review(review)
                        .name(s.getName())
                        .selection(s.getSelectOption())
                        .build());
            }
        }

        review.updateReview(
                imageUrls, editReviewData.getContent(), editReviewData.getRating(), reviewOptions);
        reviewRepository.save(review);

        return ApiResponse.OK;
    }


    /*
    리뷰 삭제
     */
    @Transactional
    public ApiResponse<Void> deleteReview(Long reviewId) {
        Review deleteReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_NOT_FOUND));

        // S3에서 삭제할 이미지 파일들
        List<String> deleteImages = deleteReview.getReviewImages().stream()
                .map(ReviewImage::getUrl)
                .toList();

        reviewRepository.deleteById(reviewId);

        for (String deleteImage : deleteImages) {
            String fileName = s3Uploader.extractFileNameFromUrl(deleteImage);
            try {
                s3Uploader.deleteFile(fileName, "review-images");
            } catch (Exception e) {
                throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR, "S3 삭제 실패: " + fileName);
            }
        }

        return ApiResponse.OK;
    }

    /*
    리뷰 추천 +1/-1
     */
    @Transactional
    public ApiResponse<Void> recommendReview(Long memberId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_NOT_FOUND));
        Member member = isExistsMember(memberId);

        // 리뷰 추천 상태 테이블 존재하는지 체크 후 없으면 생성
        List<ReviewRecommendStatus> rs = reviewRecommendStatusRepository.findByReviewIdAndMemberId(reviewId, memberId);
        ReviewRecommendStatus rrs;
        if (!rs.isEmpty()) {
            rrs = rs.get(0);
        } else {
            rrs = ReviewRecommendStatus.builder()
                    .review(review)
                    .member(member)
                    .isRecommend(false)
                    .build();
            reviewRecommendStatusRepository.save(rrs);
        }

        rrs.updateIsRecommend();
        int recommend = rrs.isRecommend() ? 1 : -1;
        review.updateRecommend(recommend);
        reviewRepository.save(review);

        return ApiResponse.OK;
    }

    /*
    type: new인 곳에 newImage를 순서대로 넣어주는 메소드
     */
    public void setNewImages(List<ImageBodyDTO> Images, List<MultipartFile> newImages) {
        if (Images != null || newImages != null) {
            int idx = 0;

            for (ImageBodyDTO imageBodyDTO : Images) {
                if ("new".equals(imageBodyDTO.getType())) {
                    imageBodyDTO.setFile(newImages.get(idx++));
                }
            }
        }
    }

    /*
    새로운 이미지만 s3에 저장
     */
    public void updateReviewImages(List<ImageBodyDTO> imageBodyDTOList) {
        // 새로운 이미지 s3에 저장
        for (ImageBodyDTO imageBodyDTO : imageBodyDTOList) {
            if ("new".equals(imageBodyDTO.getType())) {
                try {
                    String url = s3Uploader.uploadFile(imageBodyDTO.getFile(), "review-images");
                    imageBodyDTO.setFile(null);
                    imageBodyDTO.setUrl(url);
                } catch (Exception e) {
                    throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
    }

    /*
    기존 리뷰 이미지 조회
     */
    public List<String> getOriginalImages(Long reviewId) {
        return reviewImageRepository.findByReviewId(reviewId).stream()
                .map(ReviewImage::getUrl)
                .toList();
    }

    /*
    수정으로 인해 더이상 필요없는 이미지 파일 S3에서 삭제
     */
    public void deleteOriginalImages(Long reviewId, List<ImageBodyDTO> newImageUrls) {
        // 기존 이미지
        List<String> originalImages = getOriginalImages(reviewId);
        // 새로운 이미지
        List<String> newImages = newImageUrls.stream()
                .map(ImageBodyDTO::getUrl)
                .toList();

        // 삭제된 이미지 확인
        List<String> deleteImages = originalImages.stream()
                .filter(originalImage -> !newImages.contains(originalImage))
                .toList();

        // 삭제된 이미지 S3에서 삭제
        for (String deleteImage : deleteImages) {
            String fileName = s3Uploader.extractFileNameFromUrl(deleteImage);
            try {
                s3Uploader.deleteFile(fileName, "review-images");
            } catch (Exception e) {
                throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR, "S3 삭제 실패: " + fileName);
            }
        }
    }

    // 멤버 존재하는지 확인 후 return
    private Member isExistsMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND_BY_ID));
    }

    // 아이템 존재하는지 확인 후 return
    private Item isExistsItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));
    }
}
