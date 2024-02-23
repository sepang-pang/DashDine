package jpabook.dashdine.service;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.domain.user.UserRoleEnum;
import jpabook.dashdine.dto.request.SignupRequestDto;
import jpabook.dashdine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
    }
}
