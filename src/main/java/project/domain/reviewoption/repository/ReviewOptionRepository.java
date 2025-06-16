package project.domain.reviewoption.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.reviewoption.ReviewOption;

import java.util.List;

public interface ReviewOptionRepository extends JpaRepository<ReviewOption, Long> {

    List<ReviewOption> findBySubCategoryId(Long subCategoryId);
}
