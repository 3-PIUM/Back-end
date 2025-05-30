package project.global.verification.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public abstract class MailRequestDTO {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MailSend {
        @Email
        @NotBlank
        private String email;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class MailVerify {
        @Email
        @NotBlank
        private String email;
        @NotBlank
        private String code;
    }

}
