package jpabook.dashdine.repository.comment.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.dashdine.dto.response.comment.ReplyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static jpabook.dashdine.domain.comment.QReply.reply;

@Repository
@RequiredArgsConstructor
public class ReplyCustomRepositoryImpl implements ReplyCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ReplyForm> findReplyFormsByUserId(Long userId) {
        return jpaQueryFactory
                .select(Projections.constructor(ReplyForm.class,
                        reply.review.id,
                        reply.content,
                        reply.createdAt))
                .from(reply)
                .where(reply.user.id.eq(userId)
                        .and(reply.isDeleted.eq(false)))
                .fetch();
    }

}
