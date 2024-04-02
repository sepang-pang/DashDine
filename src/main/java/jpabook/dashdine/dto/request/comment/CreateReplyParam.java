package jpabook.dashdine.dto.request.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateReplyParam {
    private Long reviewId;
    private String content;
}
