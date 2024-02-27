package jpabook.dashdine.dto.request;

import lombok.Getter;

@Getter
public class PasswordChangeRequestDto {
    private String currentPassword;
    private String newPassword;
}