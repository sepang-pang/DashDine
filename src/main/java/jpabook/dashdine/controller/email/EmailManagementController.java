package jpabook.dashdine.controller.email;

import jpabook.dashdine.dto.request.user.AuthCodeVerificationRequestDto;
import jpabook.dashdine.dto.request.user.EmailAuthRequestDto;
import jpabook.dashdine.dto.request.user.EmailRequestDto;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.service.email.EmailManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
@Slf4j(topic = "EmailController")
public class EmailManagementController {

    private final EmailManagementService emailManagementService;

    // ID 찾기
    @PostMapping("/find-loginId")
    public ResponseEntity<ApiResponseDto> findLoginId(@RequestBody EmailRequestDto emailRequestDto) {
        emailManagementService.sendLoginId(emailRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto("아이디 찾기 완료", HttpStatus.OK.value()));
    }

    // 인증번호 전송
    @PostMapping("/send-authcode")
    public ResponseEntity<ApiResponseDto> sendAuthCode(@RequestBody EmailAuthRequestDto emailAuthRequestDto) {
        emailManagementService.sendAuthCode(emailAuthRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto("인증번호 발송완료", 200));
    }

    @PostMapping("/verify-authcode")
    public ResponseEntity<ApiResponseDto> verifyAuthCode(@RequestBody AuthCodeVerificationRequestDto authCodeVerificationRequestDto) {
        emailManagementService.verifyAuthCode(authCodeVerificationRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto("인증 성공", 200));
    }
}
