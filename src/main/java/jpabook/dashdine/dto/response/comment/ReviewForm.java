package jpabook.dashdine.dto.response.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.dashdine.domain.comment.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReviewForm {
    @JsonIgnore
    private Long orderId;
    private String username;
    private String restaurantName;
    private String content;
    private List<ReviewMenuForm> reviewMenuForms;

    public ReviewForm(Review review) {
        this.orderId = review.getOrder().getId();
        this.username = review.getUser().getLoginId();
        this.restaurantName = review.getRestaurant().getName();
        this.content = review.getContent();
    }
}
