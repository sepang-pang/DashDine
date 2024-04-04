package jpabook.dashdine.service.comment;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.comment.CreateReviewParam;
import jpabook.dashdine.dto.request.comment.UpdateReviewParam;
import jpabook.dashdine.dto.response.comment.RestaurantReviewForm;
import jpabook.dashdine.dto.response.comment.ReviewForm;

import java.util.List;

public interface ReviewService {

    // 리뷰 생성
    void createReview(User user, CreateReviewParam param);

    // 리뷰 조회
    List<ReviewForm> readAllReview(User user);

    // 가게 리뷰 조회
    List<RestaurantReviewForm> readAllReviewFromRestaurant(Long restaurantId);

    // 본인 가게 리뷰 조회
    List<RestaurantReviewForm> readAllReviewFromUser(User user);

    // 리뷰 수정
    ReviewForm updateReview(User user, Long reviewId, UpdateReviewParam param);

    // 리뷰 삭제
    void deletedReview(User user, Long reviewId);
}
