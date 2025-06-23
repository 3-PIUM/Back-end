package project.domain.review;

import jakarta.persistence.*;
import lombok.*;
import project.domain.common.BaseEntity;
import project.domain.item.Item;
import project.domain.member.Member;
import project.domain.review.dto.ReviewRequest;
import project.domain.review.dto.ReviewRequest.ImageBodyDTO;
import project.domain.reviewimage.ReviewImage;
import project.domain.reviewrecommendstatus.ReviewRecommendStatus;
import project.domain.selectoption.SelectOption;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewImage> reviewImages = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SelectOption> selectOptions = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewRecommendStatus> reviewRecommendStatusList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String content;

    @Builder.Default
    private Double rating = 0.0;

    @Builder.Default
    private int recommend = 0;

    public static Review createReview(Member member, Item item, String content, Double rating, List<String> reviewImages) {
        Review createdReview = Review.builder()
                .member(member)
                .item(item)
                .content(content)
                .rating(rating)
                .recommend(0)
                .build();

        if (reviewImages != null && !reviewImages.isEmpty()) {
            for (String imageUrl : reviewImages) {
                ReviewImage reviewImage = ReviewImage.builder()
                        .review(createdReview)
                        .url(imageUrl)
                        .build();
                createdReview.getReviewImages().add(reviewImage);
            }
        }

        return createdReview;
    }

    public void updateReview(List<ImageBodyDTO> reviewImages, String content, Double rating, List<SelectOption> selectOptions) {
        // 리뷰 이미지 수정(변동사항 있을시)
        // 기존 이미지 제거(동기화를 위함)
        if (reviewImages != null) {
            this.reviewImages.clear();

            for (ImageBodyDTO reviewImage : reviewImages) {
                ReviewImage newReviewImageUrl = ReviewImage.builder()
                        .review(this)
                        .url(reviewImage.getUrl())
                        .build();
                this.reviewImages.add(newReviewImageUrl);
            }
        }

        // 리뷰 내용 수정(변동사항 있을시)
        if (content != null) {
            this.content = content;
        }
        // 별점 수정(변동사항 있을시)
        if (rating != null) {
            this.rating = rating;
        }
        // 리뷰 옵션 선택사항 변경(변동사항 있을시)
        if (selectOptions != null && !selectOptions.isEmpty()) {
            this.selectOptions.clear();
            for (SelectOption selectOption : selectOptions) {
                SelectOption newSelect = SelectOption.builder()
                        .review(this)
                        .name(selectOption.getName())
                        .selection(selectOption.getSelection())
                        .build();
                this.selectOptions.add(newSelect);
            }
        }
    }

    public void updateRecommend(int recommend) {
        this.recommend = Math.max(0, this.recommend + recommend);
    }
}
