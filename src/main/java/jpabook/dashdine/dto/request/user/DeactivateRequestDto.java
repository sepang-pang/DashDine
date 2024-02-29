package jpabook.dashdine.dto.request.user;

import lombok.Getter;

@Getter
public class DeactivateRequestDto {
    private String password;

    public DeactivateRequestDto(String password) {
        this.password = password;
    }
}
