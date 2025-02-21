package wj.flab.group_wise.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.product.Product;

@Repository
public interface GroupPurchaseRepository extends JpaRepository<GroupPurchase, Long> {

    @Query("SELECT gp FROM GroupPurchase gp "
        + "JOIN FETCH gp.product p "
        + "WHERE gp.status = :status AND p = :product")
    List<GroupPurchase> findGroupPurchaseByProductAndStatus(
        GroupPurchase.Status status, Product product);

    List<GroupPurchase> findByProduct(Product product);

}
