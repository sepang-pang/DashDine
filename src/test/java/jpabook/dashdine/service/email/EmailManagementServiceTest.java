package jpabook.dashdine.service.email;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.domain.user.UserRoleEnum;
import jpabook.dashdine.dto.request.user.AuthCodeVerificationRequestDto;
import jpabook.dashdine.dto.request.user.EmailAuthRequestDto;
import jpabook.dashdine.dto.request.user.EmailRequestDto;
import jpabook.dashdine.redis.RedisUtil;
import jpabook.dashdine.service.user.query.UserQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailManagementServiceTest {

    @InjectMocks
    private EmailManagementService emailManagementService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private RedisUtil redisUtil;

    private final String emailFrom = "noreply@example.com";
    private final String userEmail = "user@example.com";
    private final String wrongEmail = "wrong@example.com";
    private final String userLoginId = "userLoginId";
    private final String authCode = "12345678";
    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailManagementService, "emailFrom", emailFrom); // @Value 어노테이션 필드 값 선언
        user = new User(userLoginId, "hashedPassword", userEmail, UserRoleEnum.CUSTOMER, any());
    }

    @Nested
    @DisplayName("로그인 ID 전송 테스트")
    class SendLoginIdTests {

        @Test
        @DisplayName("로그인 ID 전송 성공")
        void sendLoginIdSuccess() {
            // Given
            EmailRequestDto emailRequestDto = new EmailRequestDto(userEmail);
            when(userQueryService.findUserByEmail(userEmail)).thenReturn(user);

            // When
            emailManagementService.sendLoginId(emailRequestDto);

            // Then
            verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        }
    }

    @Nested
    @DisplayName("인증 코드 전송 테스트")
    class SendAuthCodeTests {

        @Test
        @DisplayName("인증 코드 전송 성공")
        void sendAuthCodeSuccess() {
            // Given
            EmailAuthRequestDto emailAuthRequestDto = new EmailAuthRequestDto(userLoginId, userEmail);
            when(userQueryService.findUser(userLoginId)).thenReturn(user);

            // When
            emailManagementService.sendAuthCode(emailAuthRequestDto);

            // Then
            verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        }

        @Test
        @DisplayName("인증 코드 전송 실패")
        void sendFailedAuthCodeSuccess() {
            // Given
            EmailAuthRequestDto emailAuthRequestDto = new EmailAuthRequestDto(userLoginId, wrongEmail);
            when(userQueryService.findUser(userLoginId)).thenReturn(user);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> emailManagementService.sendAuthCode(emailAuthRequestDto));
            verify(mailSender, times(0)).send(any(SimpleMailMessage.class));
        }

    }

    @Nested
    @DisplayName("인증 코드 검증 테스트")
    class VerifyAuthCodeTests {

        @Test
        @DisplayName("인증 코드 검증 성공")
        void verifyAuthCodeSuccess() {
            // Given
            AuthCodeVerificationRequestDto requestDto = new AuthCodeVerificationRequestDto(userLoginId, authCode);
            when(redisUtil.getStoredAuthCode(userLoginId)).thenReturn(authCode);

            // When & Then
            assertDoesNotThrow(() -> emailManagementService.verifyAuthCode(requestDto));
        }

        @Test
        @DisplayName("인증 코드 불일치로 인한 예외 발생")
        void verifyAuthCodeFailure() {
            // Given
            AuthCodeVerificationRequestDto requestDto = new AuthCodeVerificationRequestDto(userLoginId, "wrongCode");
            when(redisUtil.getStoredAuthCode(userLoginId)).thenReturn(authCode);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> emailManagementService.verifyAuthCode(requestDto));
        }
    }
}