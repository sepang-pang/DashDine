package jpabook.dashdine.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jpabook.dashdine.domain.user.UserRoleEnum;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j(topic = "CustomAuthenticationSuccessHandler")
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String targetUrl = determineTargetUrl(userDetails);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(UserDetailsImpl userDetails) {
        if (userDetails.isNewUser()) {
            log.info("새 이용자 (ID: {}) 추가 정보 입력 페이지로 이동", userDetails.getUsername());
            return "/user/profile-setup";
        } else if (userDetails.getUser().getRole() == UserRoleEnum.CUSTOMER) {
            log.info("일반 유저 (ID: {}) 메인 페이지로 이동", userDetails.getUsername());
            return "/";
        } else {
            log.info("사장 유저 (ID: {}) 사장 페이지로 이동", userDetails.getUsername());
            return "/admin";
        }
    }
}
