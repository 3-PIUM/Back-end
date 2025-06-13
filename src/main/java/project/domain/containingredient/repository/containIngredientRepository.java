package project.domain.containingredient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.containingredient.ContainIngredient;

import java.util.List;

public interface containIngredientRepository extends JpaRepository<ContainIngredient, Long> {

    List<ContainIngredient> findByItemId(Long itemId);
}
