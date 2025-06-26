package project.domain.makeupreviewoption;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;
import project.domain.makeupreviewoptionlist.MakeupReviewOptionList;
import project.domain.member.enums.Language;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MakeupReviewOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "makeupReviewOption", cascade = CascadeType.ALL, fetch = LAZY)
    private List<MakeupReviewOptionList> makeupReviewOptionList = new ArrayList<>();

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

    @Nullable
    private String krOpt4;
    @Nullable
    private String enOpt4;
    @Nullable
    private String jpOpt4;

    public String getName(String lang) {
        Language value = Language.valueOf(lang.toUpperCase());
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

    public String getOpt4(String lang) {
        Language value = Language.valueOf(lang);
        return switch (value) {
            case KR -> this.krOpt4;
            case EN -> this.enOpt4;
            case JP -> this.jpOpt4;
        };
    }
}
