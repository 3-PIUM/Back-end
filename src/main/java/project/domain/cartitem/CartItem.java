package project.domain.cartitem;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import lombok.*;
import project.domain.cart.Cart;
import project.domain.common.BaseEntity;
import project.domain.item.Item;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    private int quantity = 1;

    public static CartItem createCartItem(Cart cart, Item item, int quantity) {
        return CartItem.builder()
                .cart(cart)
                .item(item)
                .quantity(quantity)
                .build();
    }

    public void updateQuantity(int quantity) {
        this.quantity += quantity;
    }
}
