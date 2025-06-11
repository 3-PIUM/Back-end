package project.domain.member;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.domain.common.BaseEntity;
import project.domain.member.dto.MemberRequest;
import project.domain.member.dto.MemberRequest.UpdateDTO;
import project.domain.member.enums.Area;
import project.domain.member.enums.EnumUtil;
import project.domain.member.enums.Gender;
import project.domain.member.enums.Language;
import project.domain.member.enums.PersonalType;
import project.domain.member.enums.Role;
import project.domain.member.enums.SkinType;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "profile_img", length = 500)
    private String profileImg;

    @Column(nullable = false, length = 320, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    @Setter
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 255)
    private Area area = Area.KOREAN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language lang = Language.KR;

    //TODO
    // 설문을 이용한 사용자의 피부 MBTI를 저장합니다.

    @Builder
    private Member(String nickname, String profileImg, String email, String password,
        LocalDate birth, Gender gender,
        SkinType skinType, PersonalType personalType, Area area, Language lang) {
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.email = email;
        this.password = password;
        this.birth = birth;
        this.gender = gender;
        this.skinType = skinType;
        this.personalType = personalType;
        this.role = Role.USER;
        this.area = area;
        this.lang = lang;
    }


    public void updateMember(UpdateDTO updateDTO) {
        Optional.ofNullable(updateDTO.getNickname()).ifPresent(v -> this.nickname = v);
        Optional.ofNullable(updateDTO.getProfileImg()).ifPresent(v -> this.profileImg = v);
        Optional.ofNullable(updateDTO.getEmail()).ifPresent(v -> this.email = v);
        Optional.ofNullable(updateDTO.getBirth()).ifPresent(v -> this.birth = v);
        Optional.ofNullable(updateDTO.getGender())
            .ifPresent(v -> this.gender = EnumUtil.safeValueOf(Gender.class, v));
        Optional.ofNullable(updateDTO.getSkinType())
            .ifPresent(v -> this.skinType = EnumUtil.safeValueOf(SkinType.class, v));
        Optional.ofNullable(updateDTO.getPersonalType())
            .ifPresent(v -> this.personalType = EnumUtil.safeValueOf(PersonalType.class, v));
        Optional.ofNullable(updateDTO.getArea())
            .ifPresent(v -> this.area = EnumUtil.safeValueOf(Area.class, v));
        Optional.ofNullable(updateDTO.getLanguage())
            .ifPresent(v -> this.lang = Language.getLanguage(v));
    }

    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
}
