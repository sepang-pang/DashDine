package jpabook.dashdine.service.user;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.user.UserModificationParam;
import jpabook.dashdine.geo.GeometryUtil;
import jpabook.dashdine.repository.user.UserRepository;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j(topic = "UserService")
public class UserService {

    private final UserRepository userRepository;

    public void modifyUserProfile(User user, UserModificationParam param) throws ParseException {

        User findUser = userRepository.findByUserId(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Point point = GeometryUtil.calculatePoint(param.getLongitude(), param.getLatitude());

        findUser.modifyUserDetails(param, point);

        // 현재 인증 객체 가져오기
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) currentAuth.getPrincipal();

        // UserDetailsImpl 내부의 User 정보 업데이트
        userDetails.updateUser(findUser);

        // 새로운 Authentication 객체 생성
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                userDetails,
                currentAuth.getCredentials(),
                currentAuth.getAuthorities()
        );

        // SecurityContext에 업데이트된 Authentication 객체 재설정
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
