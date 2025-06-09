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

    @Column(nullable = false)
    private int questionId;

    @Column(nullable = false, length = 255)
    private String question;

    @Column(nullable = false)
    private String answer;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SkinAxis axis;

    @Enumerated(EnumType.STRING)
    private Step step;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Language language;

    private int nextQuestionId;

}
