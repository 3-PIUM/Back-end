package project.domain.areapopularitem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.domain.areapopularitem.AreaPopularItem;
import project.domain.member.enums.Area;

import java.util.List;

@Repository
public interface AreaPopularItemRepository extends JpaRepository<AreaPopularItem, Long> {

    List<AreaPopularItem> findTop30ByAreaOrderByCreatedAtDesc(Area area);
}
