package jpabook.dashdine.repository.menu;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.dashdine.domain.menu.QMenu;
import jpabook.dashdine.dto.response.menu.MenuForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static jpabook.dashdine.domain.menu.QMenu.menu;

@Repository
@RequiredArgsConstructor
public class MenuRepositoryCustomImpl implements MenuRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<MenuForm> findAllMenuFormsByRestaurantId(Long restaurantId) {
        return jpaQueryFactory
                .select(Projections.constructor(MenuForm.class,
                        menu.id,
                        menu.name,
                        menu.price,
                        menu.content,
                        menu.image,
                        menu.stackQuantity
                ))
                .from(menu)
                .where(menu.restaurant.id.eq(restaurantId)
                        .and(menu.isDeleted.eq(false)))
                .fetch();
    }
}
