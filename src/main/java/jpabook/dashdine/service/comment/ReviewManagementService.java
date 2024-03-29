package jpabook.dashdine.service.comment;

import jpabook.dashdine.domain.comment.RestaurantMenus;
import jpabook.dashdine.domain.comment.Review;
import jpabook.dashdine.domain.order.Order;
import jpabook.dashdine.domain.order.OrderMenu;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.comment.CreateReviewParam;
import jpabook.dashdine.dto.response.comment.ReviewForm;
import jpabook.dashdine.dto.response.comment.ReviewMenuForm;
import jpabook.dashdine.repository.comment.ReviewRepository;
import jpabook.dashdine.service.order.query.OrderMenuQueryService;
import jpabook.dashdine.service.order.query.OrderQueryService;
import jpabook.dashdine.service.user.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewManagementService implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderQueryService orderQueryService;
    private final OrderMenuQueryService orderMenuQueryService;
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

        return new ReviewForm(findUser.getLoginId(), review.getContent(), restaurantMenus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewForm> readAllReview(User user) {
        // 리뷰 조회
        List<Review> findReviews = reviewRepository.findReviewsByUserId(user.getId());

        // 리뷰 폼 생성
        List<ReviewForm> reviewForms = findReviews.stream()
                .map(ReviewForm::new)
                .toList();

        List<Long> orderIds = findReviews.stream()
                .map(r -> r.getOrder().getId())
                .collect(Collectors.toList());

        List<OrderMenu> findOrderMenus = orderMenuQueryService.findAllOrderMenusByOrderIds(orderIds);

        List<ReviewMenuForm> reviewMenuForms = findOrderMenus.stream()
                .map(ReviewMenuForm::new)
                .toList();

        Map<Long, List<ReviewMenuForm>> reviewMenuFormsMap = reviewMenuForms.stream()
                .collect(Collectors.groupingBy(ReviewMenuForm::getOrderId));

        reviewForms.forEach(rf -> rf.setReviewMenuForms(reviewMenuFormsMap.get(rf.getOrderId())));

        return reviewForms;
    }
}
