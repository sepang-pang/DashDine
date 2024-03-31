package jpabook.dashdine.repository.comment.custom;

import jpabook.dashdine.dto.response.comment.ReplyForm;

import java.util.List;

public interface ReplyCustomRepository {
    List<ReplyForm> findReplyFormsByUserId(Long userId);
}
