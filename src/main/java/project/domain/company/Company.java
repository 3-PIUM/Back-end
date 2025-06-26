package project.domain.company;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;
import project.domain.item.Item;

import java.util.ArrayList;
import java.util.List;
import project.domain.member.enums.Language;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String krName;

    private String enName;

    private String jpName;

    @OneToMany(mappedBy = "company")
    private List<Item> items = new ArrayList<>();

    public String getName(String lang) {
        Language language = Language.valueOf(lang.toUpperCase());
        if (language == Language.EN) {
            return enName;
        } else if (language == Language.JP) {
            return jpName;
        } else {
            return krName;
        }
    }
}
