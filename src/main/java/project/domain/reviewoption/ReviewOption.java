package project.domain.reviewoption;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;
import project.domain.reviewoptionlist.ReviewOptionList;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "reviewOption", cascade = CascadeType.ALL, fetch = LAZY)
    private List<ReviewOptionList> reviewOptionList = new ArrayList<>();

    private String name;

    private String opt1;

    private String opt2;

    private String opt3;

    @Nullable
    private String opt4;
}
