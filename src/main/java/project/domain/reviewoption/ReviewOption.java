package project.domain.reviewoption;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;
import project.domain.member.enums.Language;
import project.domain.subcategory.SubCategory;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    private String krName;
    private String enName;
    private String jpName;

    private String krOpt1;
    private String enOpt1;
    private String jpOpt1;

    private String krOpt2;
    private String enOpt2;
    private String jpOpt2;

    private String krOpt3;
    private String enOpt3;
    private String jpOpt3;

    public String getName(String lang) {
        Language value = Language.valueOf(lang);
        return switch (value) {
            case KR -> this.krName;
            case EN -> this.enName;
            case JP -> this.jpName;
        };
    }

    public String getOpt1(String lang) {
        Language value = Language.valueOf(lang);
        return switch (value) {
            case KR -> this.krOpt1;
            case EN -> this.enOpt1;
            case JP -> this.jpOpt1;
        };
    }

    public String getOpt2(String lang) {
        Language value = Language.valueOf(lang);
        return switch (value) {
            case KR -> this.krOpt2;
            case EN -> this.enOpt2;
            case JP -> this.jpOpt2;
        };
    }

    public String getOpt3(String lang) {
        Language value = Language.valueOf(lang);
        return switch (value) {
            case KR -> this.krOpt3;
            case EN -> this.enOpt3;
            case JP -> this.jpOpt3;
        };
    }

}
