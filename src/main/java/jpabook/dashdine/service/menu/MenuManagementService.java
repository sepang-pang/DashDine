package jpabook.dashdine.service.menu;

import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.menu.CreateMenuRequestDto;
import jpabook.dashdine.dto.request.menu.UpdateMenuRequestDto;
import jpabook.dashdine.dto.response.menu.ReadOptionResponseDto;
import jpabook.dashdine.dto.response.menu.ReadMenuResponseDto;
import jpabook.dashdine.dto.response.menu.UpdateMenuResponseDto;
import jpabook.dashdine.repository.menu.MenuRepository;
import jpabook.dashdine.repository.menu.OptionRepository;
import jpabook.dashdine.service.restaurant.RestaurantManagementService;
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
public class MenuManagementService {

    private final MenuRepository menuRepository;
    private final OptionRepository optionRepository;
    private final RestaurantManagementService restaurantManagementService;

    // 메뉴 생성
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

    // 메뉴 조회
    @Transactional(readOnly = true)
    public List<ReadMenuResponseDto> readAllMenu(Long restaurantId) {
        // 메뉴 조회
        List<ReadMenuResponseDto> menus = findAllMenu(restaurantId);

        if(menus.isEmpty()) {
            throw new IllegalArgumentException("메뉴가 존재하지 않습니다.");
        }

        // 조회한 메뉴에서 Id 값을 추출하여 List 에 저장
        // [1, 2] 리스트를 통해, 해당 Id 와 관련된 option 들을 모두 가지고 옴
        Map<Long, List<ReadOptionResponseDto>> optionsByMenuId = findOptionMap(findMenuIds(menus));

        // 메뉴의 options 에 Id 를 키 값으로 가지는 option value 리스트를 빼와서 저장
        menus.forEach(mr -> mr.setOptions(optionsByMenuId.get(mr.getMenuId())));

        return menus;
    }

    // 메뉴 수정
    public UpdateMenuResponseDto updateMenu(User user, Long menuId, UpdateMenuRequestDto updateMenuRequestDto) {
        // 메뉴 조회
        System.out.println("// ============== 메뉴 조회 ============== //");
        Menu menu = findOneMenu(user.getId(), menuId);

        // 메뉴 수정
        System.out.println("// ============== 메뉴 수정 ============== //");
        menu.update(updateMenuRequestDto);

        // 수정 메뉴 반환
        return new UpdateMenuResponseDto(menu);
    }

    // 메뉴 삭제
    public void deleteMenu(User user, Long menuId) {
        // 메뉴 조회
        Menu menu = findOneMenu(user.getId(), menuId);

        // 메뉴 삭제
        menu.delete();
    }

    // ========= Private 메서드 ========= //
    // 메뉴 중복 검증
    private void existMenuName(CreateMenuRequestDto createMenuRequestDto) {
        List<String> menuName = menuRepository.findMenuName(createMenuRequestDto.getRestaurantId());
        if (menuName.contains(createMenuRequestDto.getName())) {
            throw new IllegalArgumentException("이미 존재하는 메뉴입니다.");
        }
    }

    // Menu Id List 저장
    private List<Long> findMenuIds(List<ReadMenuResponseDto> menus) {
        return menus.stream()
                .map(ReadMenuResponseDto::getMenuId)
                .collect(Collectors.toList());
    }

    // Option Map 저장
    private Map<Long, List<ReadOptionResponseDto>> findOptionMap(List<Long> menuIds) {
        List<ReadOptionResponseDto> options = optionRepository.findAllOptions(menuIds);

        // options 를 정제시키는 과정
        return options.stream()
                .collect(Collectors.groupingBy(ReadOptionResponseDto::getMenuId));
    }


    // ========= Public 메서드 ========= //
    // 메뉴 조회
    public Menu findOneMenu(Long userId, Long menuId) {
        return menuRepository.findMenuByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));
    }

    public List<ReadMenuResponseDto> findAllMenu(Long restaurantId) {
        return menuRepository.findAllMenuByRestaurantId(restaurantId);
    }
}
