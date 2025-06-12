package project.domain.ingredient;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.cautionskintype.CautionSkinType;
import project.domain.common.BaseEntity;
import project.domain.containingredient.ContainIngredient;

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

    @Column(length = 500)
    private String effect;

    private int score;
}
