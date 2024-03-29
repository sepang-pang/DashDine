package jpabook.dashdine.service.comment;

import jpabook.dashdine.domain.comment.Review;
import jpabook.dashdine.domain.order.Order;
import jpabook.dashdine.domain.order.OrderMenu;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.comment.CreateReviewParam;
import jpabook.dashdine.dto.request.comment.UpdateReviewParam;
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
    public void createReview(User user, CreateReviewParam param) {
        // 주문 조회
        Order findOrder = orderQueryService.findOneOrder(param.getOrderId());

        // 유저 조회
        User findUser = userQueryService.findUser(user.getLoginId());

        // 리뷰 생성
        Review review = Review.createReview(findUser, findOrder, param);

        reviewRepository.save(review);
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

        // 리뷰 메뉴 폼 생성
        Map<Long, List<ReviewMenuForm>> reviewMenuFormsMap = getReviewMenuFormsMap(findReviews);

        // 리뷰 메뉴 폼 입력
        reviewForms.forEach(rf -> rf.setReviewMenuForms(reviewMenuFormsMap.get(rf.getOrderId())));

        return reviewForms;
    }

    @Override
    public ReviewForm updateReview(User user, Long reviewId, UpdateReviewParam param) {
        // 리뷰 조회
        Review findReview = getReview(reviewId);

        // 리뷰 수정
        findReview.updateReview(user, param);

        return new ReviewForm(findReview);
    }

    @Override
    public void deletedReview(User user, Long reviewId) {
        // 리뷰 조회
        Review findReview = getReview(reviewId);

        findReview.deleteReview();
    }

    private List<ReviewMenuForm> getReviewMenuForms(List<Review> findReviews) {
        List<Long> orderIds = findReviews.stream()
                .map(r -> r.getOrder().getId())
                .collect(Collectors.toList());

        List<OrderMenu> findOrderMenus = orderMenuQueryService.findAllOrderMenusByOrderIds(orderIds);

        return findOrderMenus.stream()
                .map(ReviewMenuForm::new)
                .toList();
    }

    private Map<Long, List<ReviewMenuForm>> getReviewMenuFormsMap(List<Review> findReviews) {
        List<ReviewMenuForm> reviewMenuForms = getReviewMenuForms(findReviews);

        return reviewMenuForms.stream()
                .collect(Collectors.groupingBy(ReviewMenuForm::getOrderId));
    }

    private Review getReview(Long reviewId) {
        return reviewRepository.findReviewByIdAndIsDeletedFalse(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }
}
