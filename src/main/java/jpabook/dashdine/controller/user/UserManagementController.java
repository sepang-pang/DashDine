package jpabook.dashdine.controller.user;

import jakarta.validation.Valid;
import jpabook.dashdine.dto.request.user.DeactivateRequestDto;
import jpabook.dashdine.dto.request.user.PasswordChangeRequestDto;
import jpabook.dashdine.dto.request.user.SignupRequestDto;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.user.UserInfoService;
import jpabook.dashdine.service.user.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j(topic = "UserManagementController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserManagementController {

    private final UserManagementService userManagementService;
    private final UserInfoService userInfoService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto, BindingResult bindingResult) {

        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if(!fieldErrors.isEmpty()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }

            return ResponseEntity.ok().body(new ApiResponseDto("회원가입 실패", 400));
        }

        userManagementService.signup(requestDto);

        return ResponseEntity.ok().body(new ApiResponseDto("회원가입 성공", 200));
    }

    // 로그아웃
    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponseDto> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userManagementService.logout(userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("로그아웃 성공", HttpStatus.OK.value()));
    }

    // 비밀번호 변경
    @PutMapping("/update-password")
    public ResponseEntity<ApiResponseDto> updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody PasswordChangeRequestDto passwordChangeRequestDto) {
        log.info("Controller 시작");
        userManagementService.updatePassword(userDetails.getUser(), passwordChangeRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto("비밀번호 변경 완료", HttpStatus.OK.value()));
    }

    // 가입한 아이디로 이메일 조회
    @GetMapping("/find-email")
    public ResponseEntity<ApiResponseDto> findPassword(@RequestParam("loginId")String loginId) {
        String email = userInfoService.getEmail(loginId);
        return ResponseEntity.ok().body(new ApiResponseDto(email, HttpStatus.OK.value()));
    }

    // 회원탈퇴
    @PatchMapping("/deactivate")
    public ResponseEntity<ApiResponseDto> deactivateUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody DeactivateRequestDto deactivateRequestDto) {
        userManagementService.deactivateUser(userDetails.getUser(), deactivateRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto("회원탈퇴 성공", HttpStatus.OK.value()));
    }

    // 회원복구
    @PatchMapping("/recover")
    public ResponseEntity<ApiResponseDto> recoverUser(@RequestParam("loginId")String loginId) {
        userManagementService.recoverUser(loginId);
        return ResponseEntity.ok().body(new ApiResponseDto("회원복구 성공", HttpStatus.OK.value()));
    }
}

