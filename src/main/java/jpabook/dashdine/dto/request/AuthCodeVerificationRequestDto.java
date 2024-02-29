package jpabook.dashdine.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthCodeVerificationRequestDto {
    private String loginId;
    private String authCode;
}
