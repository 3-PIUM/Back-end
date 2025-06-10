package project.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.review.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByItemId(Long itemId);
}
