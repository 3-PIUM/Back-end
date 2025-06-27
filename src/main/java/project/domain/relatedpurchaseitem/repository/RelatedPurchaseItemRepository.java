package project.domain.relatedpurchaseitem.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.domain.relatedpurchaseitem.RelatedPurchaseItem;

import java.util.List;

@Repository
public interface RelatedPurchaseItemRepository extends JpaRepository<RelatedPurchaseItem, Long> {

    @Query(value = """
            SELECT related_item_id FROM related_purchase_item
            WHERE item_id = :itemId
            AND skin_type = :skinType
            AND customer_segment = :customerSegment
            ORDER BY RAND()
            LIMIT 9
            """, nativeQuery = true)
    List<Long> find9RandomItemIdsBySkinTypeAndCustomerSegment(
            @Param("itemId") Long itemId,
            @Param("skinType") String skinType,
            @Param("customerSegment") String customerSegment
    );

    @Query(value = """
            SELECT related_item_id FROM related_purchase_item
            ORDER BY RAND()
            LIMIT 9
            """, nativeQuery = true)
    List<Long> find9RandomItemIds();
}
