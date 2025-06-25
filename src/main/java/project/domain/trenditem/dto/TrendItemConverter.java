package project.domain.trenditem.dto;

import project.domain.trenditem.TrendItem;

import java.util.List;

public abstract class TrendItemConverter {

    public static List<TrendItemDTO> toTrendItemDTOs(List<TrendItem> trendItemList) {
        return trendItemList.stream()
                .map(t -> TrendItemDTO.builder()
                        .itemId(t.getItemId())
                        .score(t.getScore())
                        .ranking(t.getRanking())
                        .build())
                .toList();
    }
}
