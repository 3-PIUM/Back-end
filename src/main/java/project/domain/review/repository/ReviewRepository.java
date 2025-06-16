package project.domain.review.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.domain.review.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByItemId(Long itemId);

    @Query("""
            SELECT r FROM Review r
            LEFT JOIN FETCH r.reviewRecommendStatusList rrs
            WHERE (r.item.id = :itemId)
            AND rrs.member.id = :memberId
            AND rrs.isRecommend = true
            """)
    List<Review> findBMemberRecommendedWithReviewStatus(
            @Param("itemId") Long itemId,
            @Param("memberId") Long memberId);
}
