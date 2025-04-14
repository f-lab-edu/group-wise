package wj.flab.group_wise.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase.Status;

@Repository
public interface GroupPurchaseRepository extends JpaRepository<GroupPurchase, Long> {

    @Query("SELECT gp FROM GroupPurchase gp "
        + "JOIN Product p ON gp.productId = p.id "
        + "WHERE gp.status = :status AND p.id = :productId")
    List<GroupPurchase> findGroupPurchaseByProductAndStatus(
        @Param("status") GroupPurchase.Status status,
        @Param("productId") Long productId);

    List<GroupPurchase> findByStatusAndEndDateBefore(Status status, LocalDateTime now);
}

