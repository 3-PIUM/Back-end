package project.global.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import project.domain.member.Member;
import project.global.chat.dto.ChatRequestDTO;
import project.global.chat.dto.ChatResponseDTO;
import reactor.core.publisher.Mono;


@Service
public class ChatService {

    private final WebClient webClient = WebClient.create("http://your-ml-api:8000");

    public Mono<ChatResponseDTO> sendToFastApi(ChatRequestDTO request, Member member) {
        request.setMemberId(member.getId());
        return webClient.post()
            .uri("http://fastapi/chat")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ChatResponseDTO.class);
    }
}
