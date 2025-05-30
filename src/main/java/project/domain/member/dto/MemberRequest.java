package project.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.member.Role;

public abstract class MemberRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinDTO{

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
        String  area;

        @NotNull
        String lang;

        String skinType;

        String personalType;
    }
}
