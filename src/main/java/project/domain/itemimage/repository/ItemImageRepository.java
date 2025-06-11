package project.domain.itemimage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.domain.itemimage.ItemImage;
import project.domain.itemimage.enums.ImageType;

import java.util.List;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {

    List<ItemImage> findByItemIdAndImageType(Long itemId, ImageType imageType);
    List<ItemImage> findByItemIdInAndImageType(List<Long> itemId, ImageType imageType);
}
