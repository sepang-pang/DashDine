package jpabook.dashdine.repository.comment;

import jpabook.dashdine.domain.comment.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
