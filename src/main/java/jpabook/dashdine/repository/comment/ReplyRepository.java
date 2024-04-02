package jpabook.dashdine.repository.comment;

import jpabook.dashdine.domain.comment.Reply;
import jpabook.dashdine.repository.comment.custom.ReplyCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long>, ReplyCustomRepository {

    @Query("select r from Reply r where r.id = :replyId and r.isDeleted = false")
    Optional<Reply> findReplyById(@Param("replyId") Long replyId);
}
