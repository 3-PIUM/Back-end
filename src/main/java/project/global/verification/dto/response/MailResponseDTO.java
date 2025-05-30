package project.global.verification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public abstract class MailResponseDTO {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class MailSend{
        private String responseComment;
        private Boolean status;
        private LocalDateTime timeStamp;
    }


    @Builder
    @Getter
    @AllArgsConstructor
    public static class MailVerify{
        private Boolean check;
        private  String email;
    }
}
