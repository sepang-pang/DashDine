package jpabook.dashdine.service.menu.query;

import jpabook.dashdine.dto.response.menu.MenuForm;
import jpabook.dashdine.repository.menu.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuQueryService {

    private final MenuRepository menuRepository;

    public List<MenuForm> findAllMenuForms(Long restaurantId) {
        return menuRepository.findAllMenuFormsByRestaurantId(restaurantId);
    }
}
