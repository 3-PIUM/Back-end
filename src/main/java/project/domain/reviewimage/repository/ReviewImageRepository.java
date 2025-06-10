package project.domain.reviewimage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.reviewimage.ReviewImage;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findByReviewId(Long reviewId);
}
