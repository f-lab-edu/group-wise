package wj.flab.group_wise.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import wj.flab.group_wise.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    boolean existsByUsername(String username);
}
