package jpabook.dashdine.repository.comment.custom;

import jpabook.dashdine.dto.response.comment.RestaurantReviewForm;

import java.util.List;

public interface ReviewCustomRepository {
    List<RestaurantReviewForm> findRestaurantReviewFormByRestaurantId(Long restaurantId);
}
