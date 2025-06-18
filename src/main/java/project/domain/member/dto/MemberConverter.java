package project.domain.member.dto;

import static project.domain.member.enums.EnumUtil.safeValueOf;
import static project.domain.member.enums.EnumUtil.toStringSafe;

import project.global.enums.skin.AxisType;
import project.domain.member.enums.Area;
import project.domain.member.enums.Gender;
import project.domain.member.enums.Language;
import project.domain.member.Member;
import project.global.enums.skin.PersonalType;
import project.global.enums.skin.SkinType;
import project.domain.member.dto.MemberRequest.JoinDTO;
import project.domain.member.dto.MemberResponse.DetailInfoDTO;
import project.global.util.SkinIssueUtil;

public abstract class MemberConverter {

    public static Member toEntity(JoinDTO dto) {
        return Member.builder()
            .nickname(dto.getNickname())
            .email(dto.getEmail())
            .gender(Gender.valueOf(dto.getGender()))
            .birth(dto.getBirth())
            .area(Area.valueOf(dto.getArea()))
            .personalType(safeValueOf(PersonalType.class, dto.getPersonalType()))
            .skinType(SkinType.getSkinType(dto.getSkinType()))
            .pigmentType(safeValueOf(AxisType.class, dto.getPigmentType()))
            .moistureType(safeValueOf(AxisType.class, dto.getMoistureType()))
            .reactivityType(safeValueOf(AxisType.class, dto.getReactivityType()))
            .lang(Language.getLanguage(dto.getLanguage()))
            .skinIssue(SkinIssueUtil.generateOXListFromIndexes(dto.getSkinIssue()))
            .build();
    }

    public static DetailInfoDTO toDetailInfoDTO(Member member) {
        return DetailInfoDTO.builder()
            .email(member.getEmail())
            .nickname(member.getNickname())
            .birth(member.getBirth())
            .profileImg(member.getProfileImg())
            .area(member.getArea().toString())
            .skinType(member.getSkinType() != null ? member.getSkinType().getString() : "")
            .mbtiCode(member.createMbti())
            .gender(member.getGender().toString())
            .personalType(toStringSafe(member.getPersonalType()))
            .language(member.getLang().toString())
            .skinIssue(SkinIssueUtil.generateIndexesFromOXList(member.getSkinIssue()))
            .build();
    }

}
