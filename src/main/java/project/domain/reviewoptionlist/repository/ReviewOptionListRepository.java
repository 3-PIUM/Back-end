package project.domain.reviewoptionlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.reviewoptionlist.ReviewOptionList;

public interface ReviewOptionListRepository extends JpaRepository<ReviewOptionList, Long> {
}
