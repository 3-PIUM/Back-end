package project.domain.wishlist;

import jakarta.persistence.*;
import lombok.*;
import project.domain.common.BaseEntity;
import project.domain.item.Item;
import project.domain.member.Member;

import static jakarta.persistence.GenerationType.IDENTITY;

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
