package jpabook.dashdine.service.comment;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.comment.CreateReplyParam;
import jpabook.dashdine.dto.request.comment.UpdateReplyParam;
import jpabook.dashdine.dto.response.comment.ReplyForm;

import java.util.List;

public interface ReplyService {

    // 답글 생성
    void createReply(User user, CreateReplyParam param);

    // 답글 조회
    List<ReplyForm> readAllReplies(User user);

    // 답글 수정
    void updateReply(User user, Long replyId, UpdateReplyParam param);

    // 답글 삭제
    void deleteReply(User user, Long replyId);
}
