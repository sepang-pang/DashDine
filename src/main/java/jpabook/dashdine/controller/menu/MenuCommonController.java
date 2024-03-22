package jpabook.dashdine.controller.menu;

import jpabook.dashdine.dto.response.menu.MenuDetailsForm;
import jpabook.dashdine.service.menu.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.CUSTOMER;
import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.OWNER;

@RestController
@RequiredArgsConstructor
@Secured({OWNER, CUSTOMER})
public class MenuCommonController {

    private final MenuService menuManagementService;

    // 메뉴 상세 조회
    @GetMapping("/restaurant/menu/{menuId}")
    public MenuDetailsForm readOneMenu(@PathVariable("menuId")Long menuId) {
        return menuManagementService.readOneMenu(menuId);
    }
}
