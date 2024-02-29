package jpabook.dashdine.dto.request.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDto {
    private String loginId;
    private String password;
}