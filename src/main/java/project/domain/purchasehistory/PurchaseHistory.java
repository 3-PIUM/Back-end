package project.domain.purchasehistory;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import project.domain.common.BaseEntity;
import project.domain.member.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "img_url", columnDefinition = "TEXT")
    private String imgUrl;

    private Integer discountRate;

    private String itemOption;

    @Builder
    private PurchaseHistory(Member member, Long itemId, String itemName, Integer price,
        Integer quantity, String imgUrl, String itemOption, Integer discountRate) {
        this.member = member;
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.imgUrl = imgUrl;
        this.itemOption = itemOption != null ? itemOption : "default";
        this.discountRate = discountRate != null ? discountRate : 0;
    }
}
