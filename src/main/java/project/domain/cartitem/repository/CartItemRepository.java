package project.domain.cartitem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.cartitem.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);

    List<CartItem> findByCartIdAndItemIdAndItemOption(Long cartId, Long itemId, String itemOption);

    Optional<CartItem> findFirstByCartIdAndItemIdAndItemOption(Long cartId, Long itemId, String itemOption);
}