package jpabook.dashdine.service.comment;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.comment.CreateReplyParam;
import jpabook.dashdine.dto.response.comment.ReplyForm;

import java.util.List;

public interface ReplyService {

    void createReply(User user, CreateReplyParam param);

    List<ReplyForm> readAllReplies(User user);
}
