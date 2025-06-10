package project.domain.review.dto;


import project.domain.review.Review;
import project.domain.review.dto.ReviewRequest.AddReviewDTO;
import project.domain.review.dto.ReviewRequest.EditReviewDTO;
import project.domain.review.dto.ReviewResponse.ReviewDTO;
import project.domain.review.dto.ReviewResponse.ReviewListDTO;
import project.domain.reviewimage.ReviewImage;

import java.util.List;

public abstract class ReviewConverter {

    public static ReviewDTO toReviewDTO(Review review) {

        List<String> reviewImages = review.getReviewImages().stream()
                .map(ReviewImage::getUrl)
                .toList();

        return ReviewDTO.builder()
                .reviewId(review.getId())
                .memberId(review.getMember().getId())
                .rating(review.getRating())
                .content(review.getContent())
                .reviewImages(reviewImages)
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public static ReviewListDTO toReviewListDTO(List<Review> reviews) {
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(ReviewConverter::toReviewDTO)
                .toList();

        return ReviewListDTO.builder()
                .reviews(reviewDTOs)
                .build();
    }

    public static AddReviewDTO toAddReviewDTO(Long memberId, Long itemId, String content, Double rating, List<String> reviewImages) {
        return AddReviewDTO.builder()
                .memberId(memberId)
                .itemId(itemId)
                .content(content)
                .rating(rating)
                .reviewImages(reviewImages)
                .build();
    }

    public static EditReviewDTO toEditReviewDTO(List<String> reviewImages, String content, Double rating) {
        return EditReviewDTO.builder()
                .reviewImages(reviewImages)
                .content(content)
                .rating(rating)
                .build();
    }
}
