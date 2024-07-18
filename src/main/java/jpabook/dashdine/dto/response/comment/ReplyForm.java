package jpabook.dashdine.dto.response.comment;

import jpabook.dashdine.domain.comment.Reply;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReplyForm {
    private String username;
    private String content;
    private LocalDateTime createdTime;

    public ReplyForm(Reply reply) {
        this.username = reply.getUser().getNickName();
        this.content = reply.getContent();
        this.createdTime = reply.getCreatedAt();
    }
}
