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
import project.domain.member.enums.Language;

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

    @Enumerated(EnumType.STRING)
    private Risk risk; // SAFE, CAUTION, DANGER, NONE

    private int ranking; // 1~2: 1, 3~4: 2, ..., 9~10: 5

    private String krName;

    @Nullable
    private String krRiskCategory;

    @Column(length = 500)
    private String krEffect;

    private String enName;

    @Nullable
    private String enRiskCategory;

    @Column(length = 500)
    private String enEffect;

    private String jpName;

    @Nullable
    private String jpRiskCategory;

    @Column(length = 500)
    private String jpEffect;

    public String getName(String lang) {
        Language language = Language.valueOf(lang.toUpperCase());
        if (language.equals(Language.EN)) {
            return enName;
        } else if (language.equals(Language.JP)) {
            return enName;
        } else {
            return krName;
        }
    }

    public String getEffect(String lang) {
        Language language = Language.valueOf(lang.toUpperCase());
        if (language.equals(Language.EN)) {
            return enEffect;
        } else if (language.equals(Language.JP)) {
            return enEffect;
        } else {
            return krEffect;
        }
    }

    public String getRiskCategory(String lang) {
        Language language = Language.valueOf(lang.toUpperCase());
        if (language.equals(Language.EN)) {
            return enRiskCategory;
        } else if (language.equals(Language.JP)) {
            return enRiskCategory;
        } else {
            return krRiskCategory;
        }
    }
}
