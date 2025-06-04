package project.domain.member.dto;

import jakarta.validation.constraints.NotNull;
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
        String lang;

        String skinType;

        String personalType;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDTO {

        @NotNull
        String nickname;

        @NotNull
        LocalDate birth;

        @NotNull
        String email;

        @NotNull
        String gender;

        @NotNull
        String area;

        @NotNull
        String lang;

        String profileImg;

        String skinType;

        String personalType;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdatePasswordDTO {

        @NotNull
        String password;
    }
}
