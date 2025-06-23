package project.domain.popularitem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.domain.popularitem.PopularItem;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PopularItemRepository extends JpaRepository<PopularItem, Long> {
    // 특정 시점 사이의 아이템 10개를 랭킹순으로 정렬
    List<PopularItem> findByCreatedAtBetweenOrderByRankingAsc(LocalDateTime start, LocalDateTime end);

    // 최신 10개를 랭킹순으로 정렬
    List<PopularItem> findTop10ByOrderByCreatedAtDescRankingAsc();
}
