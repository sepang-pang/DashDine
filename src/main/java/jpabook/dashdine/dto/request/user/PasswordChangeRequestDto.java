package jpabook.dashdine.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordChangeRequestDto {
    private String currentPassword;
    private String newPassword;
}