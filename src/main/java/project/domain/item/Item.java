package project.domain.item;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.aisummary.AiSummary;
import project.domain.category.Category;
import project.domain.common.BaseEntity;
import project.domain.company.Company;
import project.domain.containingredient.ContainIngredient;
import project.domain.graph.Graph;
import project.domain.inventory.Inventory;
import project.domain.item.enums.VeganType;
import project.domain.itemimage.ItemImage;
import project.domain.itemoption.ItemOption;
import project.domain.itemscore.ItemScore;
import project.domain.makeupreviewoptionlist.MakeupReviewOptionList;
import project.domain.member.enums.Language;
import project.domain.subcategory.SubCategory;
import project.domain.wishlist.WishList;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

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
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = LAZY)
    private List<MakeupReviewOptionList> makeUpReviewOptionList;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = LAZY)
    private List<Inventory> inventories = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = LAZY)
    private List<ItemImage> itemImages = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = LAZY)
    private List<AiSummary> aiSummaries = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = LAZY)
    private List<ItemScore> itemScores = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = LAZY)
    private List<ItemOption> itemOptions = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = LAZY)
    private List<ContainIngredient> containIngredients = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = LAZY)
    private List<Graph> graphs = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = LAZY)
    private List<WishList> wishLists = new ArrayList<>();

    @Column(nullable = false)
    private String krName;
    @Column(nullable = false)
    private String enName;
    @Column(nullable = false)
    private String jpName;

    private int originalPrice = 0;

    @Column(nullable = false)
    private int salePrice;

    private int discountRate = 0;

    private int totalStar = 0;

    @Enumerated(EnumType.STRING)
    private VeganType veganType;

    public String getName(String lang) {
        Language value = Language.valueOf(lang.toUpperCase());
        return switch (value) {
            case KR -> this.krName;
            case EN -> this.enName;
            case JP -> this.jpName;
        };
    }

}
