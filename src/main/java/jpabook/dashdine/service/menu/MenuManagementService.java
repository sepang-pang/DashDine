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

    private void existMenuName(CreateMenuRequestDto createMenuRequestDto) {
        String menuName = menuRepository.findMenuName(createMenuRequestDto.getRestaurantId());
        if (createMenuRequestDto.getName().equals(menuName)) {
            throw new IllegalArgumentException("이미 존재하는 메뉴입니다.");
        }
    }
}
