package project.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.item.Item;
import project.domain.item.repository.ItemRepository;
import project.domain.member.Member;
import project.domain.member.repository.MemberRepository;
import project.domain.review.Review;
import project.domain.review.dto.ReviewConverter;
import project.domain.review.dto.ReviewRequest.AddReviewDTO;
import project.domain.review.dto.ReviewRequest.EditReviewDTO;
import project.domain.review.dto.ReviewRequest.ImageDTO;
import project.domain.review.dto.ReviewResponse.ReviewDTO;
import project.domain.review.dto.ReviewResponse.ReviewListDTO;
import project.domain.review.repository.ReviewRepository;
import project.domain.reviewimage.ReviewImage;
import project.domain.reviewimage.repository.ReviewImageRepository;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;
import project.global.s3.util.S3Uploader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final S3Uploader s3Uploader;

    /*
        리뷰 조회
         */
    public ApiResponse<ReviewListDTO> getReview(Long itemId) {
        isExistsItem(itemId);

        List<Review> reviewList = reviewRepository.findByItemId(itemId);
        ReviewListDTO reviewListDTO = ReviewConverter.toReviewListDTO(reviewList);

        return ApiResponse.onSuccess(reviewListDTO);
    }

    /*
    리뷰 등록
     */
    @Transactional
    public ReviewDTO addReview(AddReviewDTO addReviewDTO) {
        Member member = isExistsMember(addReviewDTO.getMemberId());
        Item item = isExistsItem(addReviewDTO.getItemId());


        Review newReview = Review.createReview(
                member, item, addReviewDTO.getContent(), addReviewDTO.getRating(), addReviewDTO.getReviewImages());
        reviewRepository.save(newReview);

        return ReviewConverter.toReviewDTO(newReview);
    }


    /*
    리뷰 수정
     */
    @Transactional
    public ReviewDTO editReview(Long reviewId, EditReviewDTO editReviewDTO) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_NOT_FOUND));

        review.updateReview(
                editReviewDTO.getReviewImages(), editReviewDTO.getContent(), editReviewDTO.getRating());
        reviewRepository.save(review);

        return ReviewConverter.toReviewDTO(review);
    }


    /*
    리뷰 삭제
     */
    @Transactional
    public ReviewDTO deleteReview(Long reviewId) {
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

        return ReviewConverter.toReviewDTO(deleteReview);
    }

    /*
    새로운 이미지만 s3에 저장
     */
    public List<String> updateReviewImages(List<ImageDTO> imageDTOList) {
        List<String> imageUrls = new ArrayList<>();
        // null 체크
        List<ImageDTO> imageList = Optional.ofNullable(imageDTOList)
                .orElse(Collections.emptyList());


        // 기존 이미지는 s3에 저장 x, 새로운 이미지만 s3에 저장
        for (ImageDTO imageDTO : imageList) {
            if ("exist".equals(imageDTO.getType())) {
                imageUrls.add(imageDTO.getUrl());
            } else if ("new".equals(imageDTO.getType())) {
                try {
                    String url = s3Uploader.uploadFile(imageDTO.getFile(), "review-images");
                    imageUrls.add(url);
                } catch (Exception e) {
                    throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }

        return imageUrls;
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
    public void deleteOriginalImages(Long reviewId, List<String> newImageUrls) {
        // 기존 이미지 조회
        List<String> originalImages = getOriginalImages(reviewId);

        // 삭제된 이미지 확인
        List<String> deleteImages = originalImages.stream()
                .filter(originalImage -> !newImageUrls.contains(originalImage))
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
