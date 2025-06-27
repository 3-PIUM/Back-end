package project.global.chat.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import project.domain.itemimage.ItemImage;
import project.domain.itemimage.enums.ImageType;
import project.domain.itemimage.repository.ItemImageRepository;
import project.domain.member.Member;
import project.global.chat.dto.ChatRequestDTO;
import project.global.chat.dto.ChatResponseDTO;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final WebClient webClient = WebClient.create("http://3.106.232.7:8000");
    private final ItemImageRepository itemImageRepository;

    public Mono<ChatResponseDTO> sendToFastApi(ChatRequestDTO request, Member member) {
        if (member == null) {
            request.setSession_id("276520");
        } else {
            request.setSession_id(member.getId().toString());
        }

        return webClient.post()
            .uri("http://3.106.232.7:8000/chat")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ChatResponseDTO.class);
    }

    public ChatResponseDTO getImgUrl(ChatResponseDTO request) {

        request.getItemList().forEach(item -> {
            itemImageRepository.findFirstByItemIdAndImageType(item.getItemId(), ImageType.MAIN)
                .ifPresentOrElse(
                    image -> item.setImgUrl(image.getUrl()),
                    () -> item.setImgUrl(null)
                );
        });
        return request;
    }
}
