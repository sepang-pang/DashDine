package jpabook.dashdine.service.comment.query;

import jpabook.dashdine.domain.comment.Review;
import jpabook.dashdine.repository.comment.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;

    public Review findOneReview(Long reviewId) {
        return reviewRepository.findReviewByIdAndIsDeletedFalse(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }

    public List<Review> findAllReviews(List<Long> reviewIds) {
        return reviewRepository.findReviewsByReviewIdIn(reviewIds);
    }
}
