package jpabook.dashdine.domain.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Email {
    private String to;
    private String subject;
    private String message;
}
