package project.domain.trenditem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.domain.trenditem.TrendItem;

import java.util.List;

@Repository
public interface TrendItemRepository extends JpaRepository<TrendItem, Long> {
    // 최신 10개를 랭킹순으로 정렬
    List<TrendItem> findTop10ByOrderByCreatedAtDescRankingAsc();
}
