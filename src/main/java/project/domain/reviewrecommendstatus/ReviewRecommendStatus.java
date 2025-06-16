package project.domain.reviewrecommendstatus;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;
import project.domain.member.Member;
import project.domain.review.Review;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewRecommendStatus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    private boolean isRecommend;

    @Builder
    public ReviewRecommendStatus(Member member, Review review, boolean isRecommend) {
        this.member = member;
        this.review = review;
        this.isRecommend = isRecommend;
    }

    public void updateIsRecommend() {
        this.isRecommend = !this.isRecommend;
    }
}
