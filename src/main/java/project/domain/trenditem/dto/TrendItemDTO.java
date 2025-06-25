package project.domain.trenditem.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrendItemDTO {
    private Long itemId;
    private Double score;
    private Integer ranking;
}
