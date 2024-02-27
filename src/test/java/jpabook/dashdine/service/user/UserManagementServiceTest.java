package jpabook.dashdine.service.user;

import jpabook.dashdine.domain.user.PasswordManager;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.domain.user.UserRoleEnum;
import jpabook.dashdine.dto.request.PasswordChangeRequestDto;
import jpabook.dashdine.repository.UserRepository;
import jpabook.dashdine.service.PasswordManagerService;
import jpabook.dashdine.service.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @InjectMocks
    private UserManagementService userManagementService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordManagerService passwordManagerService;

    private User user;
    private PasswordChangeRequestDto validRequest;
    private String newPasswordEncoded = "encodedNewPassword";

    @BeforeEach
    void setUp() {
        user = new User("userExample", "encodedNewPassword", "email@example.com", UserRoleEnum.CUSTOMER);
        validRequest = new PasswordChangeRequestDto("currentPassword123!", "encodedNewPassword");
        lenient().when(passwordEncoder.encode(this.validRequest.getNewPassword())).thenReturn(newPasswordEncoded);
        lenient().when(userRepository.save(any(User.class))).thenReturn(user);
    }

    @Nested
    @DisplayName("비밀번호 수정 테스트")
    class UpdatePasswordTests {

        @Test
        @DisplayName("현재 비밀번호가 일치하지 않을 경우 예외를 발생")
        void testUpdatePasswordWithWrongCurrentPassword() {
            // Given
            when(passwordEncoder.matches(validRequest.getCurrentPassword(), user.getPassword())).thenReturn(false);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                userManagementService.updatePassword(user, validRequest);
            });
        }

        @Test
        @DisplayName("최근에 사용된 비밀번호로 변경 시도 시 예외 발생")
        void shouldThrowExceptionWhenPasswordIsRecentlyUsed() {
            // Given
            // PasswordManager 생성자를 사용하여 객체 생성
            PasswordManager passwordManager1 = new PasswordManager(user,"encodedOldPassword1");
            PasswordManager passwordManager2 = new PasswordManager(user,"encodedOldPassword2");
            PasswordManager passwordManager3 = new PasswordManager(user,"encodedNewPassword"); // 새 비밀번호와 일치하는 상황 시뮬레이션

            List<PasswordManager> passwordHistory = Arrays.asList(passwordManager1, passwordManager2, passwordManager3);
            when(passwordManagerService.findPasswordHistory(user)).thenReturn(passwordHistory);
            when(passwordEncoder.matches(validRequest.getCurrentPassword(), user.getPassword())).thenReturn(true);
            when(passwordEncoder.matches(validRequest.getNewPassword(), "encodedOldPassword1")).thenReturn(false);
            when(passwordEncoder.matches(validRequest.getNewPassword(), "encodedOldPassword2")).thenReturn(false);
            when(passwordEncoder.matches(validRequest.getNewPassword(), "encodedNewPassword")).thenReturn(true);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                userManagementService.updatePassword(user, validRequest);
            });
        }


        @Test
        @DisplayName("비밀번호 수정 성공")
        void updatePasswordSuccessfully() {
            // Given
            when(passwordEncoder.matches(validRequest.getCurrentPassword(), user.getPassword())).thenReturn(true);
            when(passwordManagerService.findPasswordHistory(user)).thenReturn(Collections.emptyList());

            // When
            userManagementService.updatePassword(user, validRequest);

            // Then
            verify(userRepository, times(1)).save(user);
            verify(passwordManagerService, times(1)).save(user, newPasswordEncoded);
        }
    }
}