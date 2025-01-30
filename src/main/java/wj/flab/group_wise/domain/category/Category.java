package wj.flab.group_wise.domain.category;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(of = "name")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
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
