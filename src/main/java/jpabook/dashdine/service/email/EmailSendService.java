package jpabook.dashdine.service.email;

import jpabook.dashdine.domain.common.Email;
import jpabook.dashdine.dto.request.EmailRequestDto;
import jpabook.dashdine.service.user.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EmailService")
public class EmailSendService {

    private final JavaMailSender mailSender;
    private final UserInfoService userInfoService;

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
        String loginId = userInfoService.findUserByEmail(emailRequestDto.getEmail()).getLoginId();
        log.info("로그인 아이디 추출: " + loginId);

        SimpleMailMessage message = prepareMessage(
                emailRequestDto.getEmail(),
                "귀하의 아이디 정보입니다.",
                loginId);

        mailSender.send(message);
    }
}
