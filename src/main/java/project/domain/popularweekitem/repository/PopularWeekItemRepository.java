package project.domain.popularweekitem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.domain.popularweekitem.PopularWeekItem;

import java.util.List;

@Repository
public interface PopularWeekItemRepository extends JpaRepository<PopularWeekItem, Long> {

    // 최신 20개의 데이터 조회
    List<PopularWeekItem> findTop20ByOrderByCreatedAtDesc();
}
