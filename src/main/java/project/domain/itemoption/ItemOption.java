package project.domain.itemoption;

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
public class ItemOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String krName;

    private String enName;

    private String jpName;

    public String getName(String lang) {
        Language language = Language.valueOf(lang);
        if (language == Language.JP) {
            return jpName;
        } else if (language == Language.EN) {
            return enName;
        } else {
            return krName;
        }
    }
}
