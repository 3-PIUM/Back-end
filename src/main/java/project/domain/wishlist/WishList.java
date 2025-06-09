package project.domain.wishlist;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.cart.Cart;
import project.domain.cartitem.CartItem;
import project.domain.common.BaseEntity;
import project.domain.item.Item;
import project.domain.member.Member;
import project.domain.member.repository.MemberRepository;
import project.domain.wishlist.dto.WishListResponse;
import project.domain.wishlist.repository.WishlistRepository;
import project.global.response.ApiResponse;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class WishList extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public static WishList createWishList(Member member, Item item) {
        return WishList.builder()
                .member(member)
                .item(item)
                .build();
    }
}
