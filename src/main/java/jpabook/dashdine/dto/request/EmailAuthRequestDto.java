package jpabook.dashdine.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailAuthRequestDto {
    private String loginId;
    private String email;
}
