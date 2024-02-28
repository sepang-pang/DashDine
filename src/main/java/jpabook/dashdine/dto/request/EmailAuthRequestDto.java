package jpabook.dashdine.dto.request;

import lombok.Getter;

@Getter
public class EmailAuthRequestDto {
    private String loginId;
    private String email;
}
