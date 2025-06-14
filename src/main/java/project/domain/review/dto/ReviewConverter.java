package project.domain.review.dto;


import project.domain.item.Item;
import project.domain.review.Review;
import project.domain.review.dto.ReviewRequest.AddReviewDTO;
import project.domain.review.dto.ReviewRequest.EditReviewDTO;
import project.domain.review.dto.ReviewResponse.*;
import project.domain.reviewimage.ReviewImage;
import project.domain.selectoption.SelectOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class ReviewConverter {

    public static ReviewOptionListDTO toReviewOptionListDTO(Item item) {
        return ReviewOptionListDTO.builder()
                .id(item.getId()) // 아이템 id
                .reviewOptionList(item.getReviewOptionLists().stream() // 아이템 옵션 리스트
                        .map(rol -> ReviewOptionDTO.builder()
                                .name(rol.getReviewOption().getName())
                                .options(Stream.of(
                                                rol.getReviewOption().getOpt1(),
                                                rol.getReviewOption().getOpt2(),
                                                rol.getReviewOption().getOpt3(),
                                                rol.getReviewOption().getOpt4()
                                        )
                                        .filter(Objects::nonNull)
                                        .filter(opt -> !opt.trim().isEmpty())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }

    public static ReviewDTO toReviewDTO(Review review, List<SelectOption> selectOptions) {

        List<String> reviewImages = review.getReviewImages().stream()
                .map(ReviewImage::getUrl)
                .toList();

        return ReviewDTO.builder()
                .reviewId(review.getId())
                .memberId(review.getMember().getId())
                .rating(review.getRating())
                .content(review.getContent())
                .reviewImages(reviewImages)
                .recommend(review.getRecommend())
                .options(selectOptions.stream()
                        .map(s->SelectOptionDTO.builder()
                                .name(s.getName())
                                .selectOption(s.getSelection())
                                .build())
                        .toList())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public static ReviewListDTO toReviewListDTO(List<Review> reviews, Map<Long, List<SelectOption>> selectOptionsMap) {
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(review -> {
                    List<SelectOption> reviewSelectOptions = selectOptionsMap.getOrDefault(review.getId(), new ArrayList<>());
                    return toReviewDTO(review, reviewSelectOptions);
                })
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

    public static EditReviewDTO toEditReviewDTO(List<String> reviewImages, String content,
                                                Double rating, List<SelectOption> reviewOptions) {
        return EditReviewDTO.builder()
                .reviewImages(reviewImages)
                .content(content)
                .rating(rating)
                .reviewOption(reviewOptions.stream()
                        .map((s)->SelectOptionDTO.builder()
                                .name(s.getName())
                                .selectOption(s.getSelection())
                                .build())
                        .toList())
                .build();
    }
}
