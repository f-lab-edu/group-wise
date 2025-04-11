package wj.flab.group_wise.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wj.flab.group_wise.domain.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findProductByProductNameAndSeller(String productName, String seller);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productAttributes ")
    List<Product> findAllWithAttributes();

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productStocks productStock where productStock.id = :productStockId")
    Optional<Product> findByProductStockId(Long productStockId);
}
