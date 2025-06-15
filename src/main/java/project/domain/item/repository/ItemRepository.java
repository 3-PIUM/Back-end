package project.domain.item.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.domain.item.Item;

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
            @Param("subCategoryName") String keyword,
            Pageable pageable
    );
}
