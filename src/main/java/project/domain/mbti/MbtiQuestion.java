package project.domain.mbti;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.member.enums.Language;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MbtiQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    // 위의 질문을 골랐을 경우
    private String optionOText;
    private Long optionONextId;

    // 해당 질문을 골랐을 경우
    private String optionXText;
    private Long optionXNextId;

    @Enumerated(EnumType.STRING)
    private Language language;

    @Enumerated(EnumType.STRING)
    private SkinAxis axis;

    @Enumerated(EnumType.STRING)
    private Step step;

}
