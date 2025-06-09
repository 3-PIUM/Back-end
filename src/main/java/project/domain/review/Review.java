package project.domain.review;

import jakarta.persistence.*;
import lombok.*;
import project.domain.common.BaseEntity;
import project.domain.item.Item;
import project.domain.member.Member;
import project.domain.reviewimage.ReviewImage;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String content;

    private Double rating;

    public static Review createReview(Member member, Item item, String content, Double rating, List<String> reviewImages) {
        Review createdReview = Review.builder()
                .member(member)
                .item(item)
                .content(content)
                .rating(rating)
                .build();

        if (reviewImages != null && !reviewImages.isEmpty()) {
            for (String imageUrl : reviewImages) {
                ReviewImage reviewImage = ReviewImage.createReviewImage(createdReview, imageUrl);
                createdReview.getReviewImages().add(reviewImage);
            }
        }

        return createdReview;
    }

    public void updateReview(List<String> reviewImages, String content, Double rating) {
        if (reviewImages != null && !reviewImages.isEmpty()) {
            // 기존 이미지 제거
            this.reviewImages.clear();

            // 수정한 이미지 등록
            for (String reviewImageUrl : reviewImages) {
                ReviewImage newReviewImageUrl = ReviewImage.createReviewImage(this, reviewImageUrl);
                this.reviewImages.add(newReviewImageUrl);
            }
        }
        if (content != null) {
            this.content = content;
        }
        if (rating != null) {
            this.rating = rating;
        }
    }
}
