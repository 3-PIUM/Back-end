package project.domain.reviewimage;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import project.domain.common.BaseEntity;
import project.domain.review.Review;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Lob
    @Column(length = 500)
    private String url;

    public void updateReview(String imageUrl) {
        this.url = imageUrl;
    }
}
