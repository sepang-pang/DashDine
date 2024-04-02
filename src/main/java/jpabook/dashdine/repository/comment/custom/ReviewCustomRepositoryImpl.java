package jpabook.dashdine.repository.comment.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.dashdine.dto.response.comment.RestaurantReviewForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static jpabook.dashdine.domain.comment.QReview.review;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<RestaurantReviewForm> findRestaurantReviewFormByRestaurantId(Long restaurantId) {
        return jpaQueryFactory
                .select(Projections.constructor(RestaurantReviewForm.class,
                        review.order.id,
                        review.user.loginId,
                        review.restaurant.name,
                        review.content,
                        review.reply))
                .from(review)
                .where(review.restaurant.id.eq(restaurantId)
                        .and(review.isDeleted.eq(false)))
                .fetch();
    }
}
