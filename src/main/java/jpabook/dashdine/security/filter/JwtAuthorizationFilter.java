package jpabook.dashdine.security.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.jwt.JwtUtil;
import jpabook.dashdine.redis.RedisUtil;
import jpabook.dashdine.security.userdetails.UserDetailsServiceImpl;
import jpabook.dashdine.service.user.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisUtil redisUtil;
    private final UserQueryService userQueryService;


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = jwtUtil.getJwtFromHeader(req);
        log.info("AccessToken = " + tokenValue);

        if (StringUtils.hasText(tokenValue)) {

            if (!jwtUtil.validateToken(tokenValue)) {
                log.error("Token Error");
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\": \"Invalid or expired JWT token\"}");
                refreshAccessToken(req, res);
                return;
            }

            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(req, res);
    }


    // 인증 처리
    public void setAuthentication(String loginId) {
        log.info("인증 성공");
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(loginId);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String loginId) {
        log.info("인증 객체 생성");
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginId);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // AccessToken 재발급
    public void refreshAccessToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // 사용자 유효성 검사
        // 헤더에 담긴 Access Token
        String expiredAccessToken = jwtUtil.getJwtFromHeader(req);
        String loginId = jwtUtil.getUserInfoFromToken(expiredAccessToken).getSubject();

        // 유저 정보 가져오기
        User findUser = userQueryService.findUser(loginId);

        // Refresh Token 추출
        log.info("쿠키에서 리프레시 토큰 추출");
        String refreshTokenFromCooikie = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshTokenFromCooikie = cookie.getValue();
                    break;
                }
            }
        }
        log.info("refreshToken = " + refreshTokenFromCooikie);

        // Redis 에서 Refresh Token 추출
        String refreshTokenFromRedis = redisUtil.getRefreshToken(findUser.getLoginId());

        // Refresh Token 유효성 검증
        if (!StringUtils.hasText(refreshTokenFromCooikie) || !jwtUtil.validateToken(refreshTokenFromCooikie) || !refreshTokenFromRedis.equals(refreshTokenFromCooikie)) {
            log.info("Refresh Token 만료 또는 유효하지 않음");
            redisUtil.deleteRefreshToken(findUser.getLoginId());
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("Refresh Token 만료 또는 유효하지 않음");
            return;
        }

        // 새로운 AccessToken 발급
        log.info("새로운 Access Token 발급");
        String newAccessToken = jwtUtil.createAccessToken(findUser.getLoginId(), findUser.getRole());

        // 헤더를 통해 전달
        res.addHeader(JwtUtil.AUTHORIZATION_HEADER, newAccessToken);
        log.info("재발급 완료");
        log.info("newAccessToken = " + newAccessToken);
    }
}
