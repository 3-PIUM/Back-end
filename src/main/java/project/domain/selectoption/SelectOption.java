package project.domain.selectoption;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;
import project.domain.review.Review;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    private String name;

    private String selection;

    @Builder
    public SelectOption(Review review, String name, String selection) {
        this.review = review;
        this.name = name;
        this.selection = selection;
    }

}
