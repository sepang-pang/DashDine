package jpabook.dashdine.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthCodeVerificationRequestDto {
    private String loginId;
    private String authCode;
}
