package jpabook.dashdine.service.menu;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.menu.CreateMenuParam;
import jpabook.dashdine.dto.request.menu.UpdateMenuParam;
import jpabook.dashdine.dto.response.menu.MenuDetailsForm;
import jpabook.dashdine.dto.response.menu.MenuForm;

import java.util.List;

public interface MenuService {

    // 공용 서비스
    MenuDetailsForm readOneMenu(Long menuId);

    // 사장 서비스
    void createMenu(User user, CreateMenuParam param);

    List<MenuDetailsForm> readAllMenu(Long restaurantId); // 추후 삭제 고려

    MenuForm updateMenu(User user, Long menuId, UpdateMenuParam param);

    void deleteMenu(User user, Long menuId);
}
