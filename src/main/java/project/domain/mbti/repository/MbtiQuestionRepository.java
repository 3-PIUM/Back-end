package project.domain.mbti.repository;


import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.domain.mbti.MbtiQuestion;
import project.domain.mbti.SkinAxis;
import project.domain.member.enums.Language;

public interface MbtiQuestionRepository extends JpaRepository<MbtiQuestion, Long> {

    List<MbtiQuestion> findByAxis(SkinAxis axis);

    /*
        질문의 시작을 기준으로 가져오는 쿼리
     */
    @Query("SELECT q FROM MbtiQuestion q WHERE q.axis = :axis and q.language= :lang " +
        "ORDER BY CASE WHEN q.step = 'START' THEN 0 ELSE 1 END, q.questionId ASC")
    List<MbtiQuestion> findByAxisOrderByStartFirst(
        @Param("axis") SkinAxis axis,
        @Param("lang") Language language);


}
