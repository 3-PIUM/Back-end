package project.domain.makeupreviewoption;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;
import project.domain.makeupreviewoptionlist.MakeupReviewOptionList;
import project.global.enums.skin.PersonalType;

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

    private String name;

    private String opt1;

    private String opt2;

    private String opt3;

    @Nullable
    private String opt4;
}
