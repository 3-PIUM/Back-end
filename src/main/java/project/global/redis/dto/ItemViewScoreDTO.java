package project.global.redis.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemViewScoreDTO {
    private String itemId;
    private Double score;
}
