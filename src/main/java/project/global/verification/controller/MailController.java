package project.global.verification.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(
        summary = "인증번호 이메일 전송",
        description = "사용자가 입력한 이메일에 인증번호를 생성하고 보냅니다."
    )
    @PostMapping("/send")
    public ApiResponse<MailSend> mailSend(@RequestBody MailRequestDTO.MailSend request) {
        MailSend mailSend = emailService.sendVerificationMail(request.getEmail());
        if (!mailSend.getStatus()) {
            return ApiResponse.onFailure(ErrorStatus.MAIL_NOT_SEND, mailSend);
        }
        return ApiResponse.onSuccess(mailSend);
    }

    @Operation(
        summary = "인증번호 검증",
        description = "사용자가 입력 인증번호에 대해 인증을 진행합니다."
    )
    @PostMapping("/verify")
    public ApiResponse<MailVerify> mailVerify(@RequestBody @Valid MailRequestDTO.MailVerify request) {
        MailVerify mailVerify = emailService.verifyCode(request.getEmail(), request.getCode());
        return ApiResponse.onSuccess(mailVerify);
    }

    @Operation(
        summary = "신규 비말번호 전송",
        description = "비밀번호 찾기에 신규 비밀번호를 입력한 이메일로 전송합니다."
    )
    @PostMapping("/send/temporary-password")
    public ApiResponse<MailSend> sendTemporaryPassword(@RequestBody MailRequestDTO.MailSend request) {
        MailSend mailSend = emailService.sendPassword(request.getEmail());
        if (!mailSend.getStatus()) {
            return ApiResponse.onFailure(ErrorStatus.MAIL_NOT_SEND, mailSend);
        }
        return ApiResponse.onSuccess(mailSend);
    }
}
