package project.global.chat.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestDTO {
    private String session_id;
    private String message;
    private String lang;
    private List<Long> item_ids;
}
