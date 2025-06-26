package project.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.domain.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

    @Query(value = """
                SELECT *
                FROM member m
                WHERE m.skin_type IS NOT NULL
                LIMIT 1;
            """, nativeQuery = true)
    Member findRandom();

}
