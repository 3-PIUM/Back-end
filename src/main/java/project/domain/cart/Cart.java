package project.domain.cart;


import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Lazy;
import project.domain.cartitem.CartItem;
import project.domain.common.BaseEntity;
import project.domain.member.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @OneToMany(fetch = LAZY, mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> cartItemList = new ArrayList<>();

    @Column(nullable = false)
    private Integer totalPrice = 0;

    @Builder
    public static Cart createCart(Member member) {
        return Cart.builder()
                .member(member)
                .build();
    }

    public void updateTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }
}
