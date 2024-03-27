package jpabook.dashdine.service.comment;

import jpabook.dashdine.domain.comment.RestaurantMenus;
import jpabook.dashdine.domain.comment.Review;
import jpabook.dashdine.domain.order.Order;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.comment.CreateReviewParam;
import jpabook.dashdine.dto.response.comment.ReviewForm;
import jpabook.dashdine.repository.comment.ReviewRepository;
import jpabook.dashdine.service.order.query.OrderQueryService;
import jpabook.dashdine.service.user.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewManagementService implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderQueryService orderQueryService;
    private final UserQueryService userQueryService;

    // 리뷰 생성
    @Override
    public ReviewForm createReview(User user, CreateReviewParam param) {
        // 주문 조회
        Order findOrder = orderQueryService.findOneOrder(param.getOrderId());

        // 식당 및 메뉴 이름 Custom Object 생성
        RestaurantMenus restaurantMenus = new RestaurantMenus(findOrder.getOrderMenus());

        // 유저 조회
        User findUser = userQueryService.findUser(user.getLoginId());

        // 리뷰 생성
        Review review = Review.createReview(findUser, restaurantMenus.getRestaurant(), findOrder, param);

        reviewRepository.save(review);

        return new ReviewForm(findUser.getLoginId(), review.getContent(), restaurantMenus.getMenuNames());
    }
}
