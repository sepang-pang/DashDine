package jpabook.dashdine.controller.comment;

import jpabook.dashdine.dto.request.comment.CreateReviewParam;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.dto.response.comment.ReviewForm;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.comment.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.CUSTOMER;

@RestController
@RequiredArgsConstructor
@Secured(CUSTOMER)
public class ReviewCustomerController {

    private final ReviewService reviewService;

    @PostMapping("/review")
    public ResponseEntity<ApiResponseDto> createReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @RequestBody CreateReviewParam param) {

        reviewService.createReview(userDetails.getUser(), param);

        return ResponseEntity.ok().body(new ApiResponseDto("리뷰 작성 완료", HttpStatus.OK.value()));
    }

    @GetMapping("/review")
    public List<ReviewForm> readAllReview(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reviewService.readAllReview(userDetails.getUser());
    }
}
