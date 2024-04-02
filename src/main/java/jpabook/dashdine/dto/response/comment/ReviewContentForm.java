package jpabook.dashdine.dto.response.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.dashdine.domain.comment.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReviewContentForm {
    @JsonIgnore
    private Long reviewId;
    @JsonIgnore
    private Long restaurantId;
    private String restaurantName;
    private String content;
    private LocalDateTime createdTime;

    public ReviewContentForm(Review review) {
        this.reviewId = review.getId();
        this.restaurantId = review.getRestaurant().getId();
        this.restaurantName = review.getRestaurant().getName();
        this.content = review.getContent();
        this.createdTime = review.getCreatedAt();
    }
}
