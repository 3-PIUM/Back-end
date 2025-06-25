package project.global.chat.dto;

import java.util.List;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ChatResponseDTO {

    private String output;
    private List<Item> itemList;

    @Data
    @Getter
    public static class Item {
        private String imgUrl;
        private Long itemId;
        private String itemName;
        private Integer discountPrice;
    }
}