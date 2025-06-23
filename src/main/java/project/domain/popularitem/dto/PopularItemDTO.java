package project.domain.popularitem.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PopularItemDTO {
    private Long itemId;
    private Long viewCount;
    private Integer ranking;
}
