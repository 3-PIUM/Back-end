package project.domain.relatedviewitem.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.domain.relatedviewitem.RelatedViewItem;

import java.util.List;

@Repository
public interface RelatedViewItemRepository extends JpaRepository<RelatedViewItem, Long> {

    @Query(value = """
            SELECT related_item_id FROM related_view_item
            WHERE item_id = :itemId
            AND customer_segment = :customerSegment
            ORDER BY RAND()
            LIMIT 12
            """, nativeQuery = true)
    List<Long> find12RandomRelatedItemIdsByItemIdAndCustomerSegment(
            @Param("itemId") Long itemId,
            @Param("customerSegment") String customerSegment
    );
}
