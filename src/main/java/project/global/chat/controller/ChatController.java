package project.global.chat.controller;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.domain.member.Member;
import project.global.chat.dto.ChatRequestDTO;
import project.global.chat.dto.ChatResponseDTO;
import project.global.chat.service.ChatService;
import project.global.response.ApiResponse;
import project.global.security.annotation.LoginMember;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/message") // 클라이언트에서 /chat/message로 보낼 경우 실행됨
    public ApiResponse<ChatResponseDTO> handleMessage(
        @LoginMember Member member,
        @RequestBody ChatRequestDTO request) {
        ChatResponseDTO response = chatService.sendToFastApi(request, member).block();
        return ApiResponse.onSuccess(response);
    }
}