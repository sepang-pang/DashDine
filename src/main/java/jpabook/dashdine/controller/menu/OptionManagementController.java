package jpabook.dashdine.controller.menu;

import jpabook.dashdine.dto.request.menu.CreateOptionParam;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.menu.OptionManagementService;
import jpabook.dashdine.service.menu.OptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.OWNER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner")
@Secured(OWNER)
public class OptionManagementController {

    private final OptionService optionManagementService;

    @PostMapping("/menu-option")
    public ResponseEntity<ApiResponseDto> createOption(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CreateOptionParam param) {
        optionManagementService.createOption(userDetails.getUser(), param);
        return ResponseEntity.ok().body(new ApiResponseDto("옵션 생성 성공", HttpStatus.OK.value()));
    }

    @DeleteMapping("/menu-option/{optionId}")
    public ResponseEntity<ApiResponseDto> deleteOption(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @PathVariable("optionId") Long optionId) {

        optionManagementService.deleteOption(userDetails.getUser(), optionId);

        return ResponseEntity.ok().body(new ApiResponseDto("옵션 삭제 성공", HttpStatus.OK.value()));
    }
}
