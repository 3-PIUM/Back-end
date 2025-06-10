package project.domain.wishlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.wishlist.WishList;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishList, Long> {
    List<WishList> findByMemberId(Long memberId);

    Optional<WishList> findByMemberIdAndItemId(Long memberId, Long itemId);

}
