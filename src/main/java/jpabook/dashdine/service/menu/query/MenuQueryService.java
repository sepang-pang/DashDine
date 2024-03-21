package jpabook.dashdine.service.menu.query;

import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.dto.response.menu.MenuForm;
import jpabook.dashdine.repository.menu.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuQueryService {

    private final MenuRepository menuRepository;

    public Menu findOneMenu(Long menuId) {
        return menuRepository.findMenuById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));
    }

    public List<MenuForm> findAllMenuForms(Long restaurantId) {
        return menuRepository.findMenuFormsByRestaurantId(restaurantId);
    }
}
