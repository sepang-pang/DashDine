package jpabook.dashdine.service.category;

import jpabook.dashdine.domain.restaurant.Category;
import jpabook.dashdine.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryManagementService {

    private final CategoryRepository categoryRepository;

    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }
}
