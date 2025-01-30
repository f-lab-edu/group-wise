package wj.flab.group_wise.domain.category;

import jakarta.persistence.CascadeType;
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
import lombok.EqualsAndHashCode;
import wj.flab.group_wise.domain.BaseTimeEntity;

@Entity
@EqualsAndHashCode(of = "name")
public class Category extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL, // 부모 엔티티가 삭제되면 자식 엔티티도 삭제
        orphanRemoval = true       // 부모와 연관관계가 끊어지면 자식 엔티티도 삭제
    )
    private List<Category> children = new ArrayList<>();

    public void addChildren(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("category is null");
        }

        if (this.children.contains(category)) {
            return;
        }

        this.children.add(category);
    }
}
