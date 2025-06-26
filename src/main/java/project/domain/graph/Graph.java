package project.domain.graph;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;
import project.domain.item.Item;
import project.domain.member.enums.Language;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Graph extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String krName;
    private String enName;
    private String jpName;

    private String krOptionName;
    private String enOptionName;
    private String jpOptionName;

    private int percentage;

    public String getName(String lang) {
        Language value = Language.valueOf(lang.toUpperCase());
        return switch (value) {
            case KR -> this.krName;
            case EN -> this.enName;
            case JP -> this.jpName;
        };
    }

    public String getOptionName(String lang) {
        Language value = Language.valueOf(lang.toUpperCase());
        return switch (value) {
            case KR -> this.krOptionName;
            case EN -> this.enOptionName;
            case JP -> this.jpOptionName;
        };
    }

}
