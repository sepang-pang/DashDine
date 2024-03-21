package jpabook.dashdine.service.email;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.user.AuthCodeVerificationRequestDto;
import jpabook.dashdine.dto.request.user.EmailAuthRequestDto;
import jpabook.dashdine.dto.request.user.EmailRequestDto;
import jpabook.dashdine.redis.RedisUtil;
import jpabook.dashdine.service.user.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EmailService")
public class EmailManagementService {

    private final JavaMailSender mailSender;
    private final UserQueryService userQueryService;
    private final RedisUtil redisUtil;

    @Value("${spring.mail.from.email}")
    private String emailFrom;

    // SimpleMailMessage 생성
    private SimpleMailMessage prepareMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        return message;
    }

    // 아이디 전달 로직
    public void sendLoginId(EmailRequestDto emailRequestDto) {
        String loginId = userQueryService.findUserByEmail(emailRequestDto.getEmail()).getLoginId();
        log.info("로그인 아이디 추출: " + loginId);

        SimpleMailMessage message = prepareMessage(
                emailRequestDto.getEmail(),
                "귀하의 아이디 정보입니다.",
                loginId);

        mailSender.send(message);
    }

    // 인증 코드 전송 로직
    public void sendAuthCode(EmailAuthRequestDto emailAuthRequestDto) {
        // 유저 검증
        User user = userQueryService.findUser(emailAuthRequestDto.getLoginId());

        if (!user.getEmail().equals(emailAuthRequestDto.getEmail())) {
            throw new IllegalArgumentException("입력하신 이메일 주소가 등록된 주소와 일치하지 않습니다.");
        }

        // 인증 코드 생성
        String authCode = createAuthCode();
        log.info("인증 코드 생성: " + authCode);

        // 메시지 객체 생성
        SimpleMailMessage emailForm = prepareMessage(
                emailAuthRequestDto.getEmail(),
                "인증코드",
                authCode);

        // Redis 에 인증코드 저장
        redisUtil.saveLoginIdAndAuthCode(user.getLoginId(), authCode, 3, TimeUnit.MINUTES);

        mailSender.send(emailForm);
    }

    // 인증코드 검증
    public void verifyAuthCode(AuthCodeVerificationRequestDto authCodeVerificationRequestDto) {
        // 인증코드 가져오기
        String authCode = authCodeVerificationRequestDto.getAuthCode();
        String storedAuthCode = redisUtil.getStoredAuthCode(authCodeVerificationRequestDto.getLoginId());

        // 인증코드 검증
        if (!authCode.equals(storedAuthCode)) {
            throw new IllegalArgumentException("입력하신 인증코드가 일치하지 않습니다. 다시 확인해 주세요.");
        }
    }


    // 인증코드 생성
    private String createAuthCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);

            switch (index) {
                case 0 -> key.append((char) ((int) random.nextInt(26) + 97));
                case 1 -> key.append((char) ((int) random.nextInt(26) + 65));
                case 2 -> key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }
}
