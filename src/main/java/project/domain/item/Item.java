package project.domain.item;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;

@Entity
@NoArgsConstructor
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    //TODO
    // 판매회사, 서브카테고리 연관관계 맵핑 필요

    @Column(nullable = false)
    private String name;

    private String content;

    private int originalPrice;

    @Column(nullable = false)
    private int salePrice;

    @Column(nullable = false)
    private String barcode;

}
