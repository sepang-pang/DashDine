package jpabook.dashdine.dto.response.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.dashdine.domain.comment.Reply;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantReviewForm {
    @JsonIgnore
    private Long orderId;
    private String username;
    private String restaurantName;
    private String content;
    private List<ReviewMenuForm> reviewMenuForms;
    private ReplyForm replyForm;

    // Projections
    public RestaurantReviewForm(Long orderId, String username, String restaurantName, String content, Reply reply) {
        this.orderId = orderId;
        this.username = username;
        this.restaurantName = restaurantName;
        this.content = content;
        this.replyForm = new ReplyForm(reply);
    }
}
