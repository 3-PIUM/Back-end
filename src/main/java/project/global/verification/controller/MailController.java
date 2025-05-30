package project.global.verification.controller;


import project.global.response.ApiResponse;
import project.global.response.status.ErrorStatus;
import project.global.verification.dto.request.MailRequestDTO;
import project.global.verification.dto.response.MailResponseDTO;
import project.global.verification.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static project.global.verification.dto.response.MailResponseDTO.*;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
@Slf4j
public class MailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ApiResponse<MailSend> mailSend(@RequestBody MailRequestDTO.MailSend request) {
        MailSend mailSend = emailService.sendVerificationMail(request.getEmail());
        if (!mailSend.getStatus()) {
            return ApiResponse.onFailure(ErrorStatus.MAIL_NOT_SEND, mailSend);
        }
        return ApiResponse.onSuccess(mailSend);
    }

    @PostMapping("/verify")
    public ApiResponse<MailVerify> mailVerify(@RequestBody @Valid MailRequestDTO.MailVerify request) {
        MailVerify mailVerify = emailService.verifyCode(request.getEmail(), request.getCode());
        return ApiResponse.onSuccess(mailVerify);
    }

    @PostMapping("/send/temporary-password")
    public ApiResponse<MailSend> sendTemporaryPassword(@RequestBody MailRequestDTO.MailSend request) {
        MailSend mailSend = emailService.sendPassword(request.getEmail());
        if (!mailSend.getStatus()) {
            return ApiResponse.onFailure(ErrorStatus.MAIL_NOT_SEND, mailSend);
        }
        return ApiResponse.onSuccess(mailSend);
    }
}
