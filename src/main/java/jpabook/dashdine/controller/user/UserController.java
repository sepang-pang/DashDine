package jpabook.dashdine.controller.user;

import jpabook.dashdine.dto.request.user.UserModificationParam;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.io.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestPart;

@Slf4j(topic = "UserController")
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "sign-in";
    }

    @GetMapping("/user/profile-setup")
    public String setupProfile(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        model.addAttribute("nickName", userDetails.getUser().getNickName());
        return "profile-setup";
    }

    @PatchMapping("/user/profile-setup")
    public ResponseEntity<ApiResponseDto> modifyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @RequestPart("param") UserModificationParam param) throws ParseException {

        userService.modifyUserProfile(userDetails.getUser(), param);

        return ResponseEntity.ok().body(new ApiResponseDto("프로필 수정 성공", HttpStatus.OK.value()));
    }

}




