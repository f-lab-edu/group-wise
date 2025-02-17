package wj.flab.group_wise.domain.product;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import wj.flab.group_wise.domain.BaseTimeEntity;
import wj.flab.group_wise.util.ListUtils;

@Entity
@Getter @Setter
public class Product extends BaseTimeEntity {

    public enum SaleStatus {
        SALE,       // 판매중
        SOLD_OUT,   // 품절
        DISCONTINUE // 단종
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Setter
    private String seller;                      // 판매사

    @NotBlank
    @Setter
    private String productName;                 // 상품명

    @Range(min = 0)
    @Setter
    private int basePrice;                      // 기준가(정가)

    @Enumerated(EnumType.STRING)
    private SaleStatus saleStatus;              // 판매상태

    @OneToMany(
        mappedBy = "product",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true )
    private List<ProductAttribute> productAttributes = new ArrayList<>();  // 상품의 선택항목

    @OneToMany(
        mappedBy = "product",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true )
    private List<ProductStock> productStocks = new ArrayList<>();           // 상품의 선택항목 조합에 따른 재고

    public static Product createProduct(String seller, String productName, int basePrice) {
        return new Product(seller, productName, basePrice);
    }

    protected Product() {
    }

    private Product(String seller, String productName, int basePrice) {
        this.seller = seller;
        this.productName = productName;
        this.basePrice = basePrice;
    }

    public void addProductAttribute(ProductAttribute productAttribute) {
        productAttributes.add(productAttribute);
    }

    public void generateProductStocks() {
        if (productAttributes.isEmpty()) {
            ProductStock newStock = new ProductStock(this);
            productStocks.add(newStock);
        } else {
            List<List<ProductAttributeValue>> lists = ListUtils.cartesianProduct(productAttributes);
            lists.forEach(combination -> {
                ProductStock newStock = new ProductStock(this, combination);
                productStocks.add(newStock);
            });
        }
    }


}
