package project.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.item.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
