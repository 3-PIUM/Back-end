package project.domain.user;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;

@Entity
@NoArgsConstructor(access = PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "profile_img", nullable = false, length = 100)
    private String profileImg;

    @Column(nullable = false, length = 320, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private SkinType skinType;

    @Enumerated(EnumType.STRING)
    private PersonalType personalType;

//    @Column(columnDefinition = "json")
//    @Convert(converter = JsonToMapConverter.class)
//    private Map<String, Object> skinIssue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false, length = 255)
    private String area;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language lang = Language.KOREAN;
}
