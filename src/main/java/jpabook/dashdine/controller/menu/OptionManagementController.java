package jpabook.dashdine.controller.menu;

import jpabook.dashdine.dto.request.menu.CreateOptionRequestDto;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.menu.OptionManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.OWNER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner")
@Secured(OWNER)
public class OptionManagementController {

    private final OptionManagementService optionManagementService;

    @PostMapping("/menu-option")
    public ResponseEntity<ApiResponseDto> createOption(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CreateOptionRequestDto createOptionRequestDto) {
        optionManagementService.createOption(userDetails.getUser(), createOptionRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto("옵션 생성 성공", HttpStatus.OK.value()));
    }
}
