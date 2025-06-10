package project.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.domain.member.Member;
import project.domain.review.dto.ReviewConverter;
import project.domain.review.dto.ReviewRequest.AddReviewBodyDTO;
import project.domain.review.dto.ReviewRequest.AddReviewDTO;
import project.domain.review.dto.ReviewRequest.EditReviewBodyDTO;
import project.domain.review.dto.ReviewRequest.EditReviewDTO;
import project.domain.review.dto.ReviewResponse.ReviewDTO;
import project.domain.review.dto.ReviewResponse.ReviewListDTO;
import project.domain.review.service.ReviewService;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;
import project.global.s3.service.S3Uploader;
import project.global.security.annotation.LoginMember;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "리뷰 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final S3Uploader s3Uploader;

    @Operation(
            summary = "리뷰 조회",
            description = "입력한 아이템 ID에 해당하는 아이템 리뷰를 조회합니다."
    )
    @GetMapping("/{itemId}")
    public ApiResponse<ReviewListDTO> getReview(
            @Parameter(description = "아이템 ID") @PathVariable Long itemId
    ) {
        return reviewService.getReview(itemId);
    }

    @Operation(
            summary = "리뷰 등록",
            description = "입력한 아이템 ID에 해당하는 아이템에 리뷰를 등록합니다."
    )
    @PostMapping(value = "/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ReviewDTO> addReview(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "아이템 ID") @PathVariable Long itemId,
            @Parameter(description = "리뷰 정보(사진+리뷰+평점)") @ModelAttribute AddReviewBodyDTO addReviewBodyDTO
    ) {
        try {
            List<MultipartFile> files = addReviewBodyDTO.getFiles();
            List<String> urls = new ArrayList<>();

            // s3에 리뷰 이미지 저장 후 해당 url 반환
            if (files != null) {
                urls = s3Uploader.uploadFiles(files, "review-images");
            }

            AddReviewDTO addReviewDTO = ReviewConverter
                    .toAddReviewDTO(
                            member.getId(), itemId, addReviewBodyDTO.getContent(), addReviewBodyDTO.getRating(), urls);

            ReviewDTO addedReviewDTO = reviewService.addReview(addReviewDTO);
            return ApiResponse.onSuccess("추가된 리뷰", addedReviewDTO);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "리뷰 수정",
            description = "입력한 리뷰 ID에 해당하는 리뷰 수정"
    )
    @PatchMapping(value = "/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ReviewDTO> editReview(
            @Parameter(description = "리뷰 ID") @PathVariable Long reviewId,
            @Parameter(description = "수정 내용(사진+리뷰+평점)") @ModelAttribute EditReviewBodyDTO editReviewBodyDTO
    ) {
        // 새로 추가한 이미지만 S3에 저장
        List<String> imageUrls = reviewService.updateReviewImages(editReviewBodyDTO.getReviewImages());
        // 더이상 필요없는 이미지 파일 S3에서 삭제
        reviewService.deleteOriginalImages(reviewId, imageUrls);

        EditReviewDTO editReviewDTO = ReviewConverter.toEditReviewDTO(
                imageUrls,
                editReviewBodyDTO.getContent(),
                editReviewBodyDTO.getRating()
        );

        ReviewDTO updateReview = reviewService.editReview(reviewId, editReviewDTO);

        return ApiResponse.onSuccess("수정된 리뷰", updateReview);
    }

    @Operation(
            summary = "리뷰 삭제",
            description = "입력한 리뷰 ID에 해당하는 리뷰 삭제"
    )
    @DeleteMapping("/{reviewId}")
    public ApiResponse<ReviewDTO> deleteReview(
            @Parameter(description = "삭제할 리뷰 ID") @PathVariable Long reviewId
    ) {
        ReviewDTO deleteReview = reviewService.deleteReview(reviewId);
        return ApiResponse.onSuccess("삭제된 리뷰", deleteReview);
    }
}
