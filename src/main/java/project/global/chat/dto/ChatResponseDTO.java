package project.global.chat.dto;

import java.util.List;
import lombok.Data;

@Data
public class ChatResponseDTO {

    private String output;
    private List<Item> itemList;

    @Data
    public static class Item {

        private Long item_id;
        private String item_name;
        private String item_image;
    }
}