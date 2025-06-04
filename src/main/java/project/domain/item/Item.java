package project.domain.item;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;
import project.domain.company.Company;
import project.domain.inventory.Inventory;
import project.domain.subcategory.SubCategory;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @OneToMany(mappedBy = "item")
    private List<Inventory> inventories = new ArrayList<>();


    @Column(nullable = false)
    private String name;

    private String content;

    private int originalPrice = 0;

    @Column(nullable = false)
    private int salePrice;

    @Column(nullable = false)
    private String barcode;

}
