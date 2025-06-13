package project.domain.graph.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.graph.Graph;

import java.util.List;

public interface GraphRepository extends JpaRepository<Graph, Long> {
    List<Graph> findByItemId(Long itemId);
}
