package project.domain.review.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.domain.member.Member;
import project.domain.review.dto.ReviewRequest.AddReviewBodyDTO;
import project.domain.review.dto.ReviewRequest.EditReviewBodyDTO;
import project.domain.review.dto.ReviewResponse.ReviewDTO;
import project.domain.review.dto.ReviewResponse.ReviewListDTO;
import project.domain.review.dto.ReviewResponse.ReviewOptionListDTO;
import project.domain.review.service.ReviewService;
import project.global.response.ApiResponse;
import project.global.security.annotation.LoginMember;

import java.util.List;

@Tag(name = "리뷰 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(
            summary = "리뷰 옵션 조회",
            description = "입력한 아이템 ID에 해당하는 아이템 리뷰 옵션을 조회합니다."
    )
    @GetMapping("/{itemId}/option")
    public ApiResponse<ReviewOptionListDTO> getReviewOption(
            @Parameter(description = "아이템 ID") @PathVariable Long itemId,
            @Parameter(description = "설정 언어") @RequestParam(defaultValue = "KR") String lang
    ) {
        return reviewService.getReviewOption(itemId, lang);
    }

    @Operation(
            summary = "리뷰 조회",
            description = "입력한 아이템 ID에 해당하는 아이템 리뷰를 조회합니다."
    )
    @GetMapping("/{itemId}")
    public ApiResponse<ReviewListDTO> getReview(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "아이템 ID") @PathVariable Long itemId
    ) {
        return reviewService.getReview(member, itemId);
    }

    @Operation(
            summary = "리뷰 등록",
            description = "입력한 아이템 ID에 해당하는 아이템에 리뷰를 등록합니다."
    )
    @PostMapping(value = "/{itemId}/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ReviewDTO> addReview(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "아이템 ID") @PathVariable Long itemId,
            @Parameter(
                    description = "리뷰 정보(리뷰+평점+옵션)",
                    schema = @Schema(
                            type = "string",
                            format = "textarea",
                            example = """
                                    {
                                       "content": "dummy 리뷰",
                                       "rating": 4,
                                       "selectOptions": [
                                         {"name": "피부타입", "selectOption": "건성"},
                                         {"name": "발림성", "selectOption": "좋음"},
                                         {"name": "수분감", "selectOption": "보통"}
                                       ]
                                     }
                                    """
                    )) @RequestPart("data") String reviewDataJson,
            @Parameter(description = "사진") @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        AddReviewBodyDTO reviewData = objectMapper.readValue(reviewDataJson, AddReviewBodyDTO.class);
        reviewData.setFiles(images);

        return reviewService.addReview(member.getId(), itemId, reviewData);
    }

    @Operation(
            summary = "리뷰 수정",
            description = "입력한 리뷰 ID에 해당하는 리뷰 수정"
    )
    @PatchMapping(value = "/{reviewId}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> editReview(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "리뷰 ID") @PathVariable Long reviewId,
            @Parameter(
                    description = "수정 정보(리뷰+평점+옵션)",
                    schema = @Schema(
                            example = """
                                    {
                                      "content": "수정된 리뷰 내용",
                                      "rating": 4.5,
                                      "selectOptions": [
                                        {"name": "피부타입", "selectOption": "지성"},
                                        {"name": "발림성", "selectOption": "좋음"},
                                        {"name": "수분감", "selectOption": "좋음"}
                                      ],
                                      "reviewImages": [
                                        {"type": "exist", "url": "https://s3.amazonaws.com/bucket/existing-image.jpg"},
                                        {"type": "new"}
                                      ]
                                    }
                                    """
                    ))
            @RequestPart(value = "editData") String editReviewDataJson,
            @Parameter(description = "수정 리뷰 사진") @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        EditReviewBodyDTO editReviewData = objectMapper.readValue(editReviewDataJson, EditReviewBodyDTO.class);

        return reviewService.editReview(reviewId, editReviewData, newImages);
    }

    @Operation(
            summary = "리뷰 삭제",
            description = "입력한 리뷰 ID에 해당하는 리뷰 삭제"
    )
    @DeleteMapping("/{reviewId}/remove")
    public ApiResponse<Void> deleteReview(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "삭제할 리뷰 ID") @PathVariable Long reviewId
    ) {
        return reviewService.deleteReview(reviewId);

    }

    @Operation(
            summary = "리뷰 추천수 업데이트",
            description = "리뷰 추천수를 업데이트합니다."
    )
    @PatchMapping("/recommend/{reviewId}")
    public ApiResponse<Void> updateRecommend(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(description = "리뷰 ID") @PathVariable Long reviewId
    ) {
        return reviewService.recommendReview(member.getId(), reviewId);
    }

}
