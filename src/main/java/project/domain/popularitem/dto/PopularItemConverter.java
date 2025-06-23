package project.domain.popularitem.dto;

import project.domain.popularitem.PopularItem;

import java.util.List;

public abstract class PopularItemConverter {

    public static List<PopularItemDTO> toPopularItemDTOs(List<PopularItem> items) {
        return items.stream()
                .map(i -> PopularItemDTO.builder()
                        .itemId(i.getItemId())
                        .viewCount(i.getViewCount())
                        .ranking(i.getRanking())
                        .build())
                .toList();
    }
}
