package project.domain.aisummary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.aisummary.AiSummary;

import java.util.List;

public interface AiSummaryRepository extends JpaRepository<AiSummary, Long> {

    List<AiSummary> findByItemId(Long itemId);
    List<AiSummary> findByItemIdOrderByRankingAsc(Long itemId);
}
