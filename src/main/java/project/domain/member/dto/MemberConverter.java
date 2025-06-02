package project.domain.member.dto;

import project.domain.member.Area;
import project.domain.member.Gender;
import project.domain.member.Language;
import project.domain.member.Member;
import project.domain.member.PersonalType;
import project.domain.member.SkinType;
import project.domain.member.dto.MemberRequest.JoinDTO;

public abstract class MemberConverter {

    public static Member toEntity(JoinDTO dto) {
        return Member.builder()
            .email(dto.getEmail())
            .gender(Gender.valueOf(dto.getGender()))
            .birth(dto.getBirth())
            .area(Area.valueOf(dto.getArea()))
            .personalType(PersonalType.valueOf(dto.getPersonalType()))
            .skinType(SkinType.valueOf(dto.getSkinType()))
            .lang(Language.valueOf(dto.getLang()))
            .build();
    }

}
