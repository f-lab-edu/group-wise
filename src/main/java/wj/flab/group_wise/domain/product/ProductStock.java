package wj.flab.group_wise.domain.product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ProductStock implements Purchasable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(
        mappedBy = "productStock",
        fetch = FetchType.LAZY )
    private List<ProductAttributeValue> values = new ArrayList<>(); // 상품 선택 항목에 대해 선택된 값

    @Override
    public int getPrice() {
        return product.getBasePrice()
            + values.stream()
                .mapToInt(ProductAttributeValue::getAdditionalPrice)
                .sum();
    }

    @Override
    public int getStockQuantity() {
        return stockQuantity;
    }

    public void removeStock(int quantity) {
        if (stockQuantity - quantity < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        stockQuantity -= quantity;
    }

}
