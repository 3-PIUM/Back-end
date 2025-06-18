package project.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public abstract class MemberRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinDTO {

        @NotNull
        String nickname;

        @NotNull
        LocalDate birth;

        @NotNull
        String email;

        @NotNull
        String password;

        @NotNull
        String gender;

        @NotNull
        String area;

        @NotNull
        String language;

        String skinType;

        // 색소축관한 타입
        String pigmentType;

        // 수분/유분 관한 타입
        String moistureType;

        // 반응성 관한 타입
        String reactivityType;

        String personalType;

        // 피부고민을 인덱스로 받는다.
        List<Integer> skinIssue;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDTO {

        String nickname;

        LocalDate birth;

        String email;

        String gender;

        String area;

        String language;

        String profileImg;

        String skinType;

        String pigmentType;

        String moistureType;

        String reactivityType;

        String personalType;

        List<Integer> skinIssue;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdatePasswordDTO {

        @NotNull
        String password;
    }
}
