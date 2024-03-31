package jpabook.dashdine.repository.comment;

import jpabook.dashdine.domain.comment.Reply;
import jpabook.dashdine.repository.comment.custom.ReplyCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long>, ReplyCustomRepository {
}
