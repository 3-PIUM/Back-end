package project.domain.cartitem.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.domain.cartitem.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);

    List<CartItem> findByCartMemberId(Long memberId);

    List<CartItem> findByCartIdAndItemIdAndItemOption(Long cartId, Long itemId, String itemOption);

    @Query("""
            SELECT ci FROM CartItem ci
            LEFT JOIN FETCH ci.item i
            WHERE ci.cart.id = :cartId
            AND i.id = :itemId
            AND ci.itemOption = :itemOption
            """)
    Optional<CartItem> findFirstByCartIdAndItemIdAndItemOption(
            @Param("cartId") Long cartId,
            @Param("itemId") Long itemId,
            @Param("itemOption") String itemOption
    );
}