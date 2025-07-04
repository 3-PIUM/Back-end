package project.domain.category.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    public Optional<Category> findByName(String name);
}
