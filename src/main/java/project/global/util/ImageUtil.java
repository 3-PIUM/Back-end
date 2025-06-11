package project.global.util;

import project.domain.itemimage.ItemImage;

import java.util.Map;

public abstract class ImageUtil {

    // 해당 itemId에 맞는 메인 이미지 찾기
    public static String getMainImageUrl(Long itemId, Map<Long, ItemImage> itemImageMap) {
        ItemImage mainImage = itemImageMap.get(itemId);
        return mainImage != null ? mainImage.getUrl() : null;
    }
}
