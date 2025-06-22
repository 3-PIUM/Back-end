package project.domain.item.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.domain.item.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

//    @Query("""
//            SELECT DISTINCT i FROM Item i
//            LEFT JOIN FETCH i.itemImages img
//            WHERE UPPER(i.subCategory.name) = UPPER(:subCategoryName)
//            AND (img.imageType = 'MAIN' OR img IS NULL)
//            ORDER BY (
//                    SELECT SUM(s.score)
//                    FROM ItemScore s
//                    WHERE s.item = i
//                ) DESC
//            """)
//    List<Item> findBySubCategoryNameWithMainImage(
//            @Param("subCategoryName") String subCategoryName
//    );
//
//    @Query("""
//            SELECT DISTINCT i FROM Item i
//            LEFT JOIN FETCH i.itemImages img
//            INNER JOIN i.itemScores s
//            WHERE UPPER(i.subCategory.name) = UPPER(:subCategoryName)
//            AND s.name = :skinIssue
//            AND (img.imageType = 'MAIN' OR img IS NULL)
//            ORDER BY s.score DESC
//            """)
//    List<Item> findBySubCategoryNameAndSkinIssueWithMainImage(
//            @Param("subCategoryName") String subCategoryName,
//            @Param("skinIssue") String skinIssue
//    );

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
    List<Item> findByKeywordWithMainImage(
            String keyword
    );

//    @Query("""
//            SELECT i FROM Item i
//            LEFT JOIN FETCH i.itemImages img
//            WHERE (i.veganType = 'VEGAN')
//            AND (img.imageType = 'MAIN' OR img is null)
//            AND (i.subCategory.name = :subCategoryName)
//            ORDER BY (
//                    SELECT SUM(s2.score)
//                    FROM ItemScore s2
//                    WHERE s2.item = i
//                ) DESC
//            """)
//    List<Item> findByVeganTypeWithMainImage(
//            @Param("subCategoryName") String subCategoryName
//    );
//
//    @Query("""
//            SELECT i FROM Item i
//            LEFT JOIN FETCH i.itemImages img
//            INNER JOIN i.itemScores s
//            WHERE (i.veganType = 'VEGAN')
//            AND s.name = :skinIssue
//            AND (img.imageType = 'MAIN' OR img is null)
//            AND (i.subCategory.name = :subCategoryName)
//            ORDER BY s.score DESC
//            """)
//    List<Item> findByVeganItemsAndSkinIssueWithMainImage(
//            @Param("subCategoryName") String subCategoryName,
//            @Param("skinIssue")String skinIssue
//    );

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
