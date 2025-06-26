package project.domain.popularweekitem.dto;

import lombok.Builder;
import lombok.Data;

public abstract class PopularWeekItemResponse {

    @Data
    @Builder
    public static class PopularWeekItemDTO {
        private Long itemId;
        private Double conversionRate;
    }
}
