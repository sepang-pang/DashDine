package jpabook.dashdine.service.user;

import jpabook.dashdine.domain.cart.Cart;
import jpabook.dashdine.domain.user.PasswordManager;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.domain.user.UserRoleEnum;
import jpabook.dashdine.dto.request.user.DeactivateRequestDto;
import jpabook.dashdine.dto.request.user.PasswordChangeRequestDto;
import jpabook.dashdine.dto.request.user.SignupParam;
import jpabook.dashdine.geo.GeometryUtil;
import jpabook.dashdine.redis.RedisUtil;
import jpabook.dashdine.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j(topic = "UserManagementService")
public class UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordManagerService passwordManagerService;
    private final RedisUtil redisUtil;

    // --- 회원가입 --- //
    public void signup(SignupParam param) throws ParseException {
        String loginId = param.getLoginId();
        String password = passwordEncoder.encode(param.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByLoginId(loginId);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // email 중복확인
        String email = param.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.CUSTOMER;
        if (param.isRegisterAsOwner()) {
            role = UserRoleEnum.OWNER;
        }

        // 위치 계산
        Point point = GeometryUtil.calculatePoint(param.getLongitude(), param.getLatitude());

        // 사용자 등록
        User user = new User(loginId, param.getUsername(), password, email, role, param.getStreet(), param.getStreetDetail(), param.getZipcode(), point);

        // Cart 생성
        if(role == UserRoleEnum.CUSTOMER) {
            Cart cart = new Cart(user);
            user.createCart(cart);
        }

        userRepository.save(user);

        // 최초 비밀번호 저장
        passwordManagerService.save(user, password);
    }

    // --- 로그아웃 --- //
    public void logout(User user) {
        redisUtil.deleteRefreshToken(user.getLoginId());
    }

    // --- 비밀번호 변경 --- //
    public void updatePassword(User user, PasswordChangeRequestDto passwordChangeRequestDto) {
        // 현재 비밀번호 확인
        log.info("현재 비밀번호 확인");
        if(!passwordEncoder.matches(passwordChangeRequestDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 변경 내역 조회
        log.info("비밀번호 변경 내역 조회");
        List<PasswordManager> passwordHistory = passwordManagerService.findPasswordHistory(user);

        // 최근 세 번 이내 이용 여부 확인
        log.info("최근 세 번 이내 이용 여부 확인");
        for (PasswordManager passwordManager : passwordHistory) {
            if(passwordEncoder.matches(passwordChangeRequestDto.getNewPassword(), passwordManager.getPassword())) {
                throw new IllegalArgumentException("3회 이내 사용 이력이 있는 비밀번호입니다. 다른 비밀번호로 설정해주세요.");
            }
        }

        // 변경할 비밀번호 암호화
        log.info("변경할 비밀번호 암호화");
        String newPassword = passwordEncoder.encode(passwordChangeRequestDto.getNewPassword());
        user.updatePassword(newPassword);
        userRepository.save(user);

        // 비밀번호 정보 저장
        log.info("비밀번호 정보 저장");
        passwordManagerService.save(user, newPassword);
    }

    // -- 회원탈퇴 -- //
    public void deactivateUser(User user, DeactivateRequestDto deactivateRequestDto) {

        log.info("비밀번호 검증");
        // 비밀번호 검증
        if(!passwordEncoder.matches(deactivateRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("입력한 비밀번호가 일치하지 않습니다");
        }

        log.info("회원탈퇴 진행");

        // 회원탈퇴 진행
        user.deactivateUser();

        // 회원 정보 저장
        userRepository.save(user);
    }

    // -- 회원복구 -- //
    public void recoverUser(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 회원 기능 복구
        user.recoverUser();
    }

}
