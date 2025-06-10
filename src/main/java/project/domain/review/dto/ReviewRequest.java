package project.domain.review.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public abstract class ReviewRequest {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddReviewBodyDTO {
        private List<MultipartFile> files;
        private String content;
        private Double rating;
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
    public static class ImageDTO{
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
        private List<ImageDTO> reviewImages;
        private String content;
        private Double rating;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditReviewDTO {
        private List<String> reviewImages;
        private String content;
        private Double rating;
    }
}
