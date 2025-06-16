package project.domain.item.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.domain.item.Item;
import project.domain.item.enums.VeganType;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("""
        SELECT DISTINCT i FROM Item i
        LEFT JOIN FETCH i.itemImages img
        WHERE UPPER(i.subCategory.name) = UPPER(:subCategoryName)
        AND (img.imageType = 'MAIN' OR img IS NULL)
        """)
    Page<Item> findBySubCategoryNameWithMainImage(
        @Param("subCategoryName") String subCategoryName,
        Pageable pageable
    );

    @Query("""
        SELECT DISTINCT i FROM Item i
        LEFT JOIN FETCH i.itemImages img
        WHERE UPPER(i.name) LIKE UPPER(CONCAT('%', :keyword, '%'))
        AND (img.imageType = 'MAIN' OR img IS NULL)
        ORDER BY
            CASE
                WHEN UPPER(i.name) = UPPER(:keyword) THEN 1
                WHEN UPPER(i.name) LIKE UPPER(CONCAT(:keyword, '%')) THEN 2
                WHEN UPPER(i.name) LIKE UPPER(CONCAT('%', :keyword, '%')) THEN 3
                ELSE 4
            END
        """)
    Page<Item> findByKeywordWithMainImage(
        String keyword,
        Pageable pageable
    );


    @Query("""
        SELECT i FROM Item i
        LEFT JOIN FETCH i.itemImages img
        WHERE (i.veganType = :veganType)
        AND (img.imageType = 'MAIN' OR img is null)
        AND (i.subCategory.name = :subCategoryName)
        """)
    Page<Item> findByVeganTypeWithMainImage(
        VeganType veganType,
        String subCategoryName,
        Pageable pageable);

    /*
        구매내역 순으로 10개의 아이템 추천
     */
    @Query(value = """
            SELECT * FROM item i
            WHERE (:categoryId IS NULL OR i.category_id = :categoryId)
            ORDER BY (
                SELECT COUNT(*) FROM purchase_history ph WHERE ph.item_id = i.id
            ) DESC
            LIMIT 10
        """, nativeQuery = true)
    List<Item> findTop10ItemsByCategory(@Param("categoryId") Long categoryId);
}
