package jpabook.dashdine.service;

import jpabook.dashdine.domain.user.PasswordManager;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.domain.user.UserRoleEnum;
import jpabook.dashdine.dto.request.PasswordChangeRequestDto;
import jpabook.dashdine.dto.request.SignupRequestDto;
import jpabook.dashdine.redis.RedisUtil;
import jpabook.dashdine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void signup(SignupRequestDto requestDto) {
        String loginId = requestDto.getLoginId();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByLoginId(loginId);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // email 중복확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.CUSTOMER;
        if (requestDto.isRegisterAsOwner()) {
            role = UserRoleEnum.OWNER;
        }

        // 사용자 등록
        User user = new User(loginId, password, email, role);
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
}