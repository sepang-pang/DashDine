package jpabook.dashdine.controller.comment;

import jpabook.dashdine.dto.request.comment.CreateReviewParam;
import jpabook.dashdine.dto.response.comment.ReviewForm;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.comment.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.CUSTOMER;

@RestController
@RequiredArgsConstructor
@Secured(CUSTOMER)
public class ReviewCustomerController {

    private final ReviewService reviewService;

    @PostMapping("/review")
    public ReviewForm createReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @RequestBody CreateReviewParam param) {
        return reviewService.createReview(userDetails.getUser(), param);
    }
}
