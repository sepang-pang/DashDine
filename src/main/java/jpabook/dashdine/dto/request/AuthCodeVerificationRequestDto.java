package jpabook.dashdine.dto.request;

import lombok.Getter;

@Getter
public class AuthCodeVerificationRequestDto {
    private String loginId;
    private String authCode;
}
