package project.domain.popularweekitem.dto;

import project.domain.popularweekitem.PopularWeekItem;
import project.domain.popularweekitem.dto.PopularWeekItemResponse.PopularWeekItemDTO;

import java.util.List;

public abstract class PopularWeekItemConverter {

    public static List<PopularWeekItemDTO> toPopularWeekItemDTOs(List<PopularWeekItem> popularWeekItems) {
        return popularWeekItems.stream()
                .map(p -> PopularWeekItemDTO.builder()
                        .itemId(p.getItemId())
                        .conversionRate(p.getConversionRate())
                        .build())
                .toList();
    }
}
