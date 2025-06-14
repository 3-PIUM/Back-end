package project.domain.reviewoption.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.reviewoption.ReviewOption;

public interface ReviewOptionRepository extends JpaRepository<ReviewOption, Long> {
}
