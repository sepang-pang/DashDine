package jpabook.dashdine.service.menu;

import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.menu.CreateMenuParam;
import jpabook.dashdine.dto.request.menu.UpdateMenuParam;
import jpabook.dashdine.dto.response.menu.MenuDetailsForm;
import jpabook.dashdine.dto.response.menu.OptionForm;
import jpabook.dashdine.repository.menu.MenuRepository;
import jpabook.dashdine.service.menu.query.OptionQueryService;
import jpabook.dashdine.service.restaurant.query.RestaurantQueryService;
import jpabook.dashdine.util.StringNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j(topic = "Menu Management Service Log")
public class MenuManagementService implements MenuService{

    private final MenuRepository menuRepository;
    private final OptionQueryService optionQueryService;
    private final RestaurantQueryService restaurantQueryService;

    // 메뉴 생성
    @Override
    public void createMenu(User user, CreateMenuParam param) {
        // 메뉴 중복 조회
        existMenuName(param.getRestaurantId(), param.getName());

        // 가게 조회
        Restaurant findRestaurant = restaurantQueryService.findOneRestaurant(user, param.getRestaurantId());
        
        // 메뉴 생성
        Menu menu = Menu.CreateMenu(param, findRestaurant);

        // 메뉴 저장
        menuRepository.save(menu);
    }

    // 메뉴 조회 (전체)
    @Transactional(readOnly = true)
    @Override
    public List<MenuDetailsForm> readAllMenu(Long restaurantId) {
        // 메뉴 조회
        List<MenuDetailsForm> menus = findAllMenuDetailsForms(restaurantId);

        // 조회한 메뉴에서 Id 값을 추출하여 List 에 저장
        // [1, 2] 리스트를 통해, 해당 Id 와 관련된 option 들을 모두 가지고 옴
        Map<Long, List<OptionForm>> optionsMap = getOptionMap(getMenuIds(menus));

        // 메뉴의 options 에 Id 를 키 값으로 가지는 option value 리스트를 빼와서 저장
        menus.forEach(mr -> mr.setOptions(optionsMap.get(mr.getMenuId())));

        return menus;
    }

    // 메뉴 조회 (단일)
    @Transactional(readOnly = true)
    @Override
    public MenuDetailsForm readOneMenu(Long menuId) {
        // 메뉴 조회
        MenuDetailsForm findMenu = menuRepository.findMenuDetailsFormById(menuId);

        // 옵션 조회 후 menu 에 삽입
        findMenu.setOptions(optionQueryService.findAllOptionForms(findMenu));

        return findMenu;
    }

    // 메뉴 수정
    @Override
    public void updateMenu(User user, Long menuId, UpdateMenuParam param) {
        existMenuName(param.getRestaurantId(), param.getName());

        // 메뉴 조회
        Menu menu = findOneMenu(menuId);

        // 메뉴 수정
        menu.updateMenu(user, param);
    }

    // 메뉴 삭제
    @Override
    public void deleteMenu(User user, Long menuId) {
        // 메뉴 조회
        Menu menu = findOneMenu(menuId);

        // 메뉴 삭제
        menu.deleteMenu(user);
    }

    // === 조회 메서드 === //
    // 메뉴 조회
    private Menu findOneMenu(Long menuId) {
        return menuRepository.findMenuById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));
    }

    // 메뉴 폼 반환
    private List<MenuDetailsForm> findAllMenuDetailsForms(Long restaurantId) {
        return menuRepository.findMenuDetailsFormsByRestaurantId(restaurantId);
    }

    // === 검증 메서드 === //
    // 메뉴 중복 검증
    private void existMenuName(Long paramId, String paramName) {
        List<String> findMenuNames = menuRepository.findMenuNameByRestaurantId(paramId);
        String normalizedRequestName = StringNormalizer.normalizeString(paramName);
        for (String menuName : findMenuNames) {
            if (StringNormalizer.normalizeString(menuName).equals(normalizedRequestName)) {
                throw new IllegalArgumentException("동일한 메뉴가 존재합니다.");
            }
        }
    }

    // === 데이터 처리 메서드 === //
    // Menu Id List 저장
    private List<Long> getMenuIds(List<MenuDetailsForm> menus) {
        return menus.stream()
                .map(MenuDetailsForm::getMenuId)
                .collect(Collectors.toList());
    }

    // Option Map 저장
    private Map<Long, List<OptionForm>> getOptionMap(List<Long> menuIds) {
        List<OptionForm> options = optionQueryService.findAllOptionForms(menuIds);

        // options 를 정제시키는 과정
        return options.stream()
                .collect(Collectors.groupingBy(OptionForm::getMenuId));
    }

}
