package jpabook.dashdine.repository.comment;

import jpabook.dashdine.domain.comment.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review r " +
            "left join fetch r.restaurant " +
            "left join fetch r.order " +
            "where r.user.id = :userId")
    List<Review> findReviewsByUserId(@Param("userId") Long userId);
}
