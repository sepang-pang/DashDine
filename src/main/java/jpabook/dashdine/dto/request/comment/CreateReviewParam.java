package jpabook.dashdine.dto.request.comment;

import lombok.Getter;

@Getter
public class CreateReviewParam {
    private Long restaurantId;
    private Long orderId;
    private String content;
    private String image;
}
