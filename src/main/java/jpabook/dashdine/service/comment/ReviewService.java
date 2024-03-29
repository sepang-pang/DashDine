package jpabook.dashdine.service.comment;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.comment.CreateReviewParam;
import jpabook.dashdine.dto.response.comment.ReviewForm;

import java.util.List;

public interface ReviewService {

    // 리뷰 생성
    void createReview(User user, CreateReviewParam param);

    // 리뷰 조회
    List<ReviewForm> readAllReview(User user);
}
