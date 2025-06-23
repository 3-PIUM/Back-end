package project.global.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import project.domain.member.Member;
import project.global.chat.dto.ChatRequestDTO;
import project.global.chat.dto.ChatResponseDTO;
import reactor.core.publisher.Mono;


@Service
public class ChatService {

    private final WebClient webClient = WebClient.create("http://52.79.241.142:8000");

    public Mono<ChatResponseDTO> sendToFastApi(ChatRequestDTO request, Member member) {
        request.setSession_id(member.getEmail());
        return webClient.post()
            .uri("http://52.79.241.142:8000/chat")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ChatResponseDTO.class);
    }
}
