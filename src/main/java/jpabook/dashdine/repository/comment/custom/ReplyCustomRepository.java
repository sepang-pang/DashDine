package jpabook.dashdine.repository.comment.custom;

import jpabook.dashdine.dto.response.comment.ReplyDetailsForm;

import java.util.List;

public interface ReplyCustomRepository {
    List<ReplyDetailsForm> findReplyFormsByUserId(Long userId);
}
