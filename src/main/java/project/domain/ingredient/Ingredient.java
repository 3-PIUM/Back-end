package project.domain.ingredient;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.cautionskintype.CautionSkinType;
import project.domain.common.BaseEntity;
import project.domain.containingredient.ContainIngredient;
import project.domain.ingredient.enums.Risk;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ingredient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "ingredient")
    private List<ContainIngredient> containIngredients = new ArrayList<>();

    @OneToMany(mappedBy = "ingredient")
    private List<CautionSkinType> cautionSkinTypes = new ArrayList<>();

    private String name;

    @Enumerated(EnumType.STRING)
    private Risk risk; // SAFE, CAUTION, DANGER, NONE

    @Nullable
    private String riskCategory;

    @Column(length = 500)
    private String effect;

    private int ranking; // 1~2: 1, 3~4: 2, ..., 9~10: 5
}
