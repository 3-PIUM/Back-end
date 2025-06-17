package project.domain.reviewrecommendstatus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.reviewrecommendstatus.ReviewRecommendStatus;

import java.util.List;

public interface ReviewRecommendStatusRepository extends JpaRepository<ReviewRecommendStatus, Long> {

    List<ReviewRecommendStatus> findByReviewIdAndMemberId(Long reviewId, Long memberId);

}
