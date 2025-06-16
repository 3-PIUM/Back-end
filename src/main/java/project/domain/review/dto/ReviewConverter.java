package project.domain.review.dto;


import project.domain.item.Item;
import project.domain.makeupreviewoption.MakeupReviewOption;
import project.domain.review.Review;
import project.domain.review.dto.ReviewRequest.AddReviewDTO;
import project.domain.review.dto.ReviewRequest.EditReviewDTO;
import project.domain.review.dto.ReviewResponse.*;
import project.domain.reviewimage.ReviewImage;
import project.domain.reviewoption.ReviewOption;
import project.domain.selectoption.SelectOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class ReviewConverter {

    public static ReviewOptionListDTO toMakeupReviewOptionListDTO(Item item, List<MakeupReviewOption> reviewOptions) {
        return ReviewOptionListDTO.builder()
                .id(item.getId())
                .reviewOptionList(reviewOptions.stream()
                        .map(r -> ReviewOptionDTO.builder()
                                .name(r.getName())
                                .options(Stream.of(
                                                r.getOpt1(),
                                                r.getOpt2(),
                                                r.getOpt3(),
                                                r.getOpt4()
                                        )
                                        .filter(Objects::nonNull)
                                        .filter(opt -> !opt.trim().isEmpty())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }

    public static ReviewOptionListDTO toReviewOptionListDTO(Item item, List<ReviewOption> reviewOptions) {
        return ReviewOptionListDTO.builder()
                .id(item.getId())
                .reviewOptionList(
                        reviewOptions.stream()
                                .map(r -> ReviewOptionDTO.builder()
                                        .name(r.getName())
                                        .options(Stream.of(
                                                        r.getOpt1(),
                                                        r.getOpt2(),
                                                        r.getOpt3()
                                                ).toList()
                                        ).build())
                                .toList()
                )
                .build();
    }

    public static ReviewDTO toReviewDTO(Review review, List<SelectOption> selectOptions, List<Long> recommendedIds) {
        return ReviewDTO.builder()
                .reviewId(review.getId())
                .memberId(review.getMember().getId())
                .rating(review.getRating())
                .content(review.getContent())
                .reviewImages(review.getReviewImages().stream()
                        .map(ReviewImage::getUrl)
                        .toList())
                .recommend(review.getRecommend())
                .isRecommend(recommendedIds.contains(review.getId()))
                .options(selectOptions.stream()
                        .map(s -> SelectOptionDTO.builder()
                                .name(s.getName())
                                .selectOption(s.getSelection())
                                .build())
                        .toList())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    // 옵션 세팅 메소드
    public static List<SelectOption> getSelectOption(Review r, Map<Long, List<SelectOption>> selectOptionsMap) {
        return selectOptionsMap.getOrDefault(r.getId(), new ArrayList<>());
    }

    public static ReviewListDTO toReviewListDTO(
            List<Review> reviews,
            Map<Long, List<SelectOption>> selectOptionsMap,
            List<Long> recommendedIds
    ) {
        return ReviewListDTO.builder()
                .reviews(reviews.stream()
                        .map(review -> ReviewDTO.builder()
                                .reviewId(review.getId())
                                .memberId(review.getMember().getId())
                                .rating(review.getRating())
                                .content(review.getContent())
                                .reviewImages(review.getReviewImages().stream()
                                        .map(ReviewImage::getUrl)
                                        .toList())
                                .recommend(review.getRecommend())
                                .isRecommend(recommendedIds.contains(review.getId()))
                                .options(ReviewConverter.getSelectOption(review, selectOptionsMap).stream()
                                        .map(s -> SelectOptionDTO.builder()
                                                .name(s.getName())
                                                .selectOption(s.getSelection())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
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
                        .map((s) -> SelectOptionDTO.builder()
                                .name(s.getName())
                                .selectOption(s.getSelection())
                                .build())
                        .toList())
                .build();
    }
}
