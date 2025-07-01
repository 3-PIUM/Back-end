package project.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public abstract class ReviewResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewOptionDTO{
        private String name;
        private List<String> options;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewOptionListDTO {
        private Long id;
        private List<ReviewOptionDTO> reviewOptionList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectOptionDTO {
        private String name;
        private String selectOption;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewDTO {
        private Long reviewId;
        private Long memberId;
        private String memberName;
        private String content;
        private Double rating;
        private List<String> reviewImages;
        private int recommend;
        private List<SelectOptionDTO> options;
        @JsonProperty("isRecommend")
        private boolean isRecommend;
        private LocalDate updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewListDTO {
        private List<ReviewDTO> reviews;
    }
}
