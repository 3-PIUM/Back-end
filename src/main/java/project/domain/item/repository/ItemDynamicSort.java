package project.domain.item.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import project.domain.item.Item;
import project.domain.item.QItem;
import project.domain.item.enums.VeganType;
import project.domain.itemimage.QItemImage;
import project.domain.itemimage.enums.ImageType;
import project.domain.itemscore.QItemScore;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemDynamicSort {

    private final JPAQueryFactory queryFactory;

    // 일반 아이템(비건X)
    public List<Item> findItemsWithDSL(
            String subCategoryName,
            String skinIssue,
            String priceSort
    ) {
        QItem item = QItem.item;
        QItemImage img = QItemImage.itemImage;
        QItemScore score = QItemScore.itemScore;

        JPAQuery<Item> query = queryFactory
                .selectFrom(item)
                .leftJoin(item.itemImages, img).fetchJoin()
                .where(
                        item.subCategory.name.eq(subCategoryName),
                        img.imageType.eq(ImageType.valueOf("MAIN")).or(img.isNull())
                );

        // 스킨이슈 조건 동적 추가
        setDynamicOptions(skinIssue, priceSort, query, item, score);

        return query.fetch();
    }

    // 비건 아이템
    public List<Item> findVeganItemsWithDSL(
            String subCategoryName,
            String skinIssue,
            String priceSort
    ) {
        QItem item = QItem.item;
        QItemImage img = QItemImage.itemImage;
        QItemScore score = QItemScore.itemScore;

        JPAQuery<Item> query = queryFactory
                .selectFrom(item)
                .leftJoin(item.itemImages, img).fetchJoin()
                .where(
                        item.veganType.eq(VeganType.VEGAN),
                        item.subCategory.name.eq(subCategoryName),
                        img.imageType.eq(ImageType.valueOf("MAIN")).or(img.isNull())
                );

        // 스킨이슈 조건 동적 추가
        setDynamicOptions(skinIssue, priceSort, query, item, score);

        return query.fetch();
    }

    private static void setDynamicOptions(String skinIssue, String priceSort, JPAQuery<Item> query, QItem item, QItemScore score) {
        if (StringUtils.hasText(skinIssue)) {
            query.innerJoin(item.itemScores, score)
                    .where(score.name.eq(skinIssue),
                            score.score.ne(0));
        }

        // 정렬 조건 동적 추가
        if ("PRICE_ASC".equals(priceSort)) {
            query.orderBy(item.salePrice.asc());
        } else if ("PRICE_DESC".equals(priceSort)) {
            query.orderBy(item.salePrice.desc());
        } else if (StringUtils.hasText(skinIssue)) {
            // 스킨이슈가 있으면 해당 점수순
            query.orderBy(score.score.desc());
        } else {
            // 기본: 별점순
            query.orderBy(item.totalStar.desc());
        }
    }
}
