package project.domain.review.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import project.domain.review.dto.ReviewResponse.SelectOptionDTO;

import java.util.List;

public abstract class ReviewRequest {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddReviewBodyDTO {
        private String content;
        private Double rating;
        private List<SelectOptionDTO> selectOptions;
        private List<MultipartFile> files;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddReviewDTO {
        private Long memberId;
        private Long itemId;
        private List<String> reviewImages;
        private String content;
        private Double rating;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageBodyDTO {
        private String type;
        private String url;
        private MultipartFile file;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditReviewBodyDTO {
        private List<ImageBodyDTO> reviewImages;
        private String content;
        private Double rating;
        private List<SelectOptionDTO> selectOptions;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditReviewDTO {
        private List<String> reviewImages;
        private String content;
        private Double rating;
        private List<SelectOptionDTO> reviewOption;
    }
}
