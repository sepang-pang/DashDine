package jpabook.dashdine.controller.menu;

import jpabook.dashdine.dto.request.menu.CreateMenuParam;
import jpabook.dashdine.dto.request.menu.UpdateMenuParam;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.dto.response.menu.MenuDetailsForm;
import jpabook.dashdine.dto.response.menu.MenuForm;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.menu.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.OWNER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner")
@Secured(OWNER)
public class MenuOwnerController {

    private final MenuService menuManagementService;

    // 메뉴 생성
    @PostMapping("/menu")
    public ResponseEntity<ApiResponseDto> createMenu(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody CreateMenuParam param) {
        menuManagementService.createMenu(userDetails.getUser(), param);
        return ResponseEntity.ok().body(new ApiResponseDto("메뉴 생성 성공", HttpStatus.OK.value()));
    }

    // 메뉴 조회
    @GetMapping("/restaurant/{restaurantId}/menu")
    public List<MenuDetailsForm> readAllMenu(@PathVariable("restaurantId") Long restaurantId) {
        return menuManagementService.readAllMenu(restaurantId);
    }

    // 메뉴 수정
    @PutMapping("/menu/{menuId}")
    public ResponseEntity<ApiResponseDto> updateMenu(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable("menuId") Long menuId,
                                                     @RequestBody UpdateMenuParam param) {

        menuManagementService.updateMenu(userDetails.getUser(), menuId, param);

        return ResponseEntity.ok().body(new ApiResponseDto("메뉴 수정 성공", HttpStatus.OK.value()));
    }

    // 메뉴 삭제
    @PatchMapping("/menu/{menuId}")
    public ResponseEntity<ApiResponseDto> deleteMenu(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable("menuId") Long menuId) {
        menuManagementService.deleteMenu(userDetails.getUser(), menuId);
        return ResponseEntity.ok().body(new ApiResponseDto("메뉴 삭제 성공", HttpStatus.OK.value()));
    }
}
