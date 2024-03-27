package jpabook.dashdine.service.comment;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.comment.CreateReviewParam;
import jpabook.dashdine.dto.response.comment.ReviewForm;

public interface ReviewService {

    // 리뷰 생성
    ReviewForm createReview(User user, CreateReviewParam param);
}
