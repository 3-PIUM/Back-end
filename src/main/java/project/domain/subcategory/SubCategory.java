package project.domain.subcategory;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;
import project.domain.item.Item;
import project.domain.makeupreviewoptionlist.MakeupReviewOptionList;
import project.domain.reviewoption.ReviewOption;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "subCategory", fetch = LAZY)
    private List<Item> item = new ArrayList<>();

    @OneToMany(mappedBy = "subCategory", fetch = LAZY)
    private List<ReviewOption> reviewOptionList = new ArrayList<>();

    @Column(nullable = false)
    private String name;

}
