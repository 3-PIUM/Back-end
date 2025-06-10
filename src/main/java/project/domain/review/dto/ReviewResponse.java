package project.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.reviewimage.ReviewImage;

import java.time.LocalDateTime;
import java.util.List;

public abstract class ReviewResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewDTO {
        private Long reviewId;
        private Long memberId;
        private String content;
        private Double rating;
        private List<String> reviewImages;
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewListDTO {
        private List<ReviewDTO> reviews;
    }
}
