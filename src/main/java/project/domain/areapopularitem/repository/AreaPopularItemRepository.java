package project.domain.areapopularitem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.domain.areapopularitem.AreaPopularItem;

@Repository
public interface AreaPopularItemRepository extends JpaRepository<AreaPopularItem, Long> {
}
