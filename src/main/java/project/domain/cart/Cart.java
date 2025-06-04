package project.domain.cart;


import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @OneToMany(mappedBy = "cart", fetch = LAZY)
    private List<CartItem> cartItemList = new ArrayList<>();

    @Column(nullable = false)
    private Integer totalPrice = 0;

    public static Cart createCart(Member member) {
        Cart cart = new Cart();
        cart.member = member;
        return cart;
    }

    public void updateTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }
}
