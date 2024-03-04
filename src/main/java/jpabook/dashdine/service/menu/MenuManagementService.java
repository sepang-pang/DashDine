package jpabook.dashdine.service.menu;

import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.menu.CreateMenuRequestDto;
import jpabook.dashdine.repository.menu.MenuRepository;
import jpabook.dashdine.service.restaurant.RestaurantManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j(topic = "Menu Management Service Log")
public class MenuManagementService {

    private final MenuRepository menuRepository;
    private final RestaurantManagementService restaurantManagementService;

    public void createMenu(User user, CreateMenuRequestDto createMenuRequestDto) {
        // 메뉴 중복 조회
        System.out.println("// ============== 메뉴 중복 검사 ============== //");
        existMenuName(createMenuRequestDto);

        // 가게 조회
        System.out.println("// ============== 가게 조회 ============== //");
        Restaurant findRestaurant = restaurantManagementService.getRestaurant(user, createMenuRequestDto.getRestaurantId());

        // 메뉴 생성
        System.out.println("// ============== 메뉴 생성 ============== //");
        Menu menu = Menu.builder()
                .name(createMenuRequestDto.getName())
                .price(createMenuRequestDto.getPrice())
                .content(createMenuRequestDto.getContent())
                .image(createMenuRequestDto.getImage())
                .stackQuantity(createMenuRequestDto.getStackQuantity())
                .restaurant(findRestaurant)
                .build();

        // 메뉴 저장
        System.out.println("// ============== 메뉴 저장 ============== //");
        menuRepository.save(menu);
    }

    // ========= Private 메서드 ========= //
    // 메뉴 중복 검증
    private void existMenuName(CreateMenuRequestDto createMenuRequestDto) {
        List<String> menuName = menuRepository.findMenuName(createMenuRequestDto.getRestaurantId());
        if (menuName.contains(createMenuRequestDto.getName())) {
            throw new IllegalArgumentException("이미 존재하는 메뉴입니다.");
        }
    }

    // ========= Public 메서드 ========= //
    // 메뉴 조회
    public Menu getMenu(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));
    }
}
