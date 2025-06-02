package project.domain.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.cart.Cart;
import project.domain.member.Member;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByMember(Member member);
    Optional<Cart> findByMemberId(Long memberId);
}