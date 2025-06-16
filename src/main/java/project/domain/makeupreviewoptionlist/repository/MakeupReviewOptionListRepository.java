package project.domain.makeupreviewoptionlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.domain.makeupreviewoption.MakeupReviewOption;
import project.domain.makeupreviewoptionlist.MakeupReviewOptionList;

import java.util.List;

public interface MakeupReviewOptionListRepository extends JpaRepository<MakeupReviewOptionList, Long> {

    @Query("""
            SELECT s FROM MakeupReviewOptionList s
            LEFT JOIN FETCH s.makeupReviewOption r
            WHERE (s.item.id = :itemId)
            """)
    List<MakeupReviewOptionList> findByItemIdWithReviewOptions(Long itemId);
}
