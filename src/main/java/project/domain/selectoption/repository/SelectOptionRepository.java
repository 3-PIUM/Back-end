package project.domain.selectoption.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.domain.selectoption.SelectOption;

import java.util.List;

public interface SelectOptionRepository extends JpaRepository<SelectOption, Long> {
    List<SelectOption> findByReviewIdIn(List<Long> reviewId);

}
