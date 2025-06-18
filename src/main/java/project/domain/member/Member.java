package project.domain.member;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.domain.common.BaseEntity;
import project.global.converter.CustomStringListConverter;
import project.global.enums.skin.AxisType;
import project.domain.member.dto.MemberRequest.UpdateDTO;
import project.domain.member.enums.Area;
import project.domain.member.enums.EnumUtil;
import project.domain.member.enums.Gender;
import project.domain.member.enums.Language;
import project.domain.purchasehistory.PurchaseHistory;
import project.domain.reviewrecommendstatus.ReviewRecommendStatus;
import project.global.enums.skin.PersonalType;
import project.domain.member.enums.Role;
import project.global.enums.skin.SkinType;
import project.global.util.SkinIssueUtil;

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

    // 피부상태
    @Enumerated(EnumType.STRING)
    private SkinType skinType;

    // 색소축
    @Enumerated(EnumType.STRING)
    private AxisType pigmentType;

    // 수분/유분
    @Enumerated(EnumType.STRING)
    private AxisType moistureType;

    // 반응성
    @Enumerated(EnumType.STRING)
    private AxisType reactivityType;

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
    private Area area = Area.KOREA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language lang = Language.KR;

    @Convert(converter = CustomStringListConverter.class)
    @Column(name = "skin_issue")
    private List<String> skinIssue;


    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseHistory> purchaseHistoryList = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewRecommendStatus> reviewRecommendStatusList = new ArrayList<>();

    //TODO
    // 설문을 이용한 사용자의 피부 MBTI를 저장합니다.

    @Builder
    private Member(String nickname, String profileImg, String email, String password,
        LocalDate birth, Gender gender, AxisType pigmentType, AxisType moistureType,
        AxisType reactivityType, List<String> skinIssue,
        SkinType skinType, PersonalType personalType, Area area, Language lang) {
        this.skinIssue = skinIssue;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.email = email;
        this.password = password;
        this.birth = birth;
        this.gender = gender;
        this.skinType = skinType;
        this.pigmentType = pigmentType;
        this.moistureType = moistureType;
        this.reactivityType = reactivityType;
        this.personalType = personalType;
        this.role = Role.USER;
        this.area = area;
        this.lang = lang;
    }


    public void updateMember(UpdateDTO updateDTO) {
        Optional.ofNullable(updateDTO.getNickname()).ifPresent(v -> this.nickname = v);
        Optional.ofNullable(updateDTO.getEmail()).ifPresent(v -> this.email = v);
        Optional.ofNullable(updateDTO.getBirth()).ifPresent(v -> this.birth = v);
        Optional.ofNullable(updateDTO.getSkinIssue())
            .ifPresent(v -> this.skinIssue = SkinIssueUtil.generateOXListFromIndexes(v));
        Optional.ofNullable(updateDTO.getGender())
            .ifPresent(v -> this.gender = EnumUtil.safeValueOf(Gender.class, v));
        Optional.ofNullable(updateDTO.getSkinType())
            .ifPresent(v -> this.skinType = SkinType.getSkinType(v));
        Optional.ofNullable(updateDTO.getPigmentType())
            .ifPresent(v -> this.pigmentType = EnumUtil.safeValueOf(AxisType.class, v));
        Optional.ofNullable(updateDTO.getMoistureType())
            .ifPresent(v -> moistureType = EnumUtil.safeValueOf(AxisType.class, v));
        Optional.ofNullable(updateDTO.getReactivityType())
            .ifPresent(v -> reactivityType = EnumUtil.safeValueOf(AxisType.class, v));
        Optional.ofNullable(updateDTO.getPersonalType())
            .ifPresent(v -> this.personalType = EnumUtil.safeValueOf(PersonalType.class, v));
        Optional.ofNullable(updateDTO.getArea())
            .ifPresent(v -> this.area = EnumUtil.safeValueOf(Area.class, v));
        Optional.ofNullable(updateDTO.getLanguage())
            .ifPresent(v -> this.lang = Language.getLanguage(v));
    }

    /*
        사용자의 이미지 업로드를 위해 사용합니다.
     */
    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    /*
        사용자의 타입을 조합하여 mbti를 만들어줍니다.
     */
    public String createMbti() {
        if (Stream.of(this.skinType, this.pigmentType, this.moistureType, this.reactivityType)
            .anyMatch(Objects::isNull)) {
            return "";
        }

        return Stream.of(skinType, pigmentType, moistureType, reactivityType)
            .filter(Objects::nonNull)
            .map(Enum::name)
            .collect(Collectors.joining(", "));

    }
}
