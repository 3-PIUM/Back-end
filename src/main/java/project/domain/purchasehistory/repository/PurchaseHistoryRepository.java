package project.domain.purchasehistory.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.domain.purchasehistory.PurchaseHistory;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {

    List<PurchaseHistory> findByMemberId(Long memberId);

    @Query("SELECT ph FROM PurchaseHistory ph WHERE ph.member.id = :memberId AND DATE(ph.createdAt) = :date")
    List<PurchaseHistory> findByCreatedAtAndMemberId(
        @Param("memberId") Long memberId,
        @Param("date") LocalDate date);
}
