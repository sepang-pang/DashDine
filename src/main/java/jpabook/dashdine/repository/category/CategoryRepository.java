package jpabook.dashdine.repository.category;

import jpabook.dashdine.domain.restaurant.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
