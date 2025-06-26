package project.domain.aisummary;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;
import project.domain.item.Item;
import project.domain.member.enums.Language;
import project.global.enums.skin.SkinType;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiSummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private String krTitle;

    private String enTitle;

    private String jpTitle;

    private String krItemOption;

    private String krContent;

    private String krOriginalContent;

    private String enItemOption;

    private String enContent;

    private String enOriginalContent;

    private String jpItemOption;

    private String jpContent;

    private String jpOriginalContent;

    private int ranking;

    public String getTitle(String lang) {
        Language language = Language.valueOf(lang.toUpperCase());
        if (language == Language.JP) {
            return jpTitle;
        } else if (language == Language.KR) {
            return krTitle;
        } else {
            return enTitle;
        }
    }

    public String getItemOption(String lang) {
        Language language = Language.valueOf(lang.toUpperCase());
        if (language.equals(Language.EN)) {
            return this.enItemOption;
        } else if (language.equals(Language.JP)) {
            return this.jpItemOption;
        } else {
            return this.krItemOption;
        }
    }

    public String getContent(String lang) {
        Language language = Language.valueOf(lang.toUpperCase());
        if (language.equals(Language.EN)) {
            return this.enContent;
        } else if (language.equals(Language.JP)) {
            return this.jpContent;
        } else {
            return this.krContent;
        }
    }

    public String getOriginalContent(String lang) {
        Language language = Language.valueOf(lang.toUpperCase());
        if (language.equals(Language.EN)) {
            return this.enOriginalContent;
        } else if (language.equals(Language.JP)) {
            return this.jpOriginalContent;
        } else {
            return this.krOriginalContent;
        }
    }

}
