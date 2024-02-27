package jpabook.dashdine.controller;

import jakarta.validation.Valid;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.dto.request.SignupRequestDto;
import jpabook.dashdine.service.UserSignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserSignUpService userSignUpService;

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

        userSignUpService.signup(requestDto);

        return ResponseEntity.ok().body(new ApiResponseDto("회원가입 성공", 200));
    }
}

