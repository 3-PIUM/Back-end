package project.domain.member.dto;

import static project.domain.member.enums.EnumUtil.safeValueOf;
import static project.domain.member.enums.EnumUtil.toStringSafe;

import project.domain.member.enums.Area;
import project.domain.member.enums.Gender;
import project.domain.member.enums.Language;
import project.domain.member.Member;
import project.domain.member.enums.PersonalType;
import project.domain.member.enums.SkinType;
import project.domain.member.dto.MemberRequest.JoinDTO;
import project.domain.member.dto.MemberResponse.DetailInfoDTO;

public abstract class MemberConverter {

    public static Member toEntity(JoinDTO dto) {
        return Member.builder()
            .nickname(dto.getNickname())
            .email(dto.getEmail())
            .gender(Gender.valueOf(dto.getGender()))
            .birth(dto.getBirth())
            .area(Area.valueOf(dto.getArea()))
            .personalType(safeValueOf(PersonalType.class,dto.getPersonalType()))
            .skinType(safeValueOf(SkinType.class,dto.getSkinType()))
            .lang(Language.getLanguage(dto.getLanguage()))
            .build();
    }

    public static DetailInfoDTO toDetailInfoDTO(Member member) {
        return DetailInfoDTO.builder()
            .email(member.getEmail())
            .nickname(member.getNickname())
            .birth(member.getBirth())
            .profileImg(member.getProfileImg())
            .area(member.getArea().toString())
            .skinType(toStringSafe(member.getSkinType()))
            .gender(member.getGender().toString())
            .personalType(toStringSafe(member.getPersonalType()))
            .language(member.getLang().toString())
            .build();
    }

}
