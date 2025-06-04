package project.domain.cart;


import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import lombok.*;
import org.apache.logging.log4j.util.Lazy;
import project.domain.cartitem.CartItem;
import project.domain.common.BaseEntity;
import project.domain.member.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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

    public static Cart createCart(Member member) {
        return Cart.builder()
                .member(member)
                .totalPrice(0)
                .build();
    }

    public void updateTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }
}
