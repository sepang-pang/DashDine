package jpabook.dashdine.dto.response.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReplyDetailsForm {

    @JsonIgnore
    private Long reviewId;
    private String content;
    private LocalDateTime createdTime;
    private ReviewContentForm reviewContentForm;

    // Projections
    public ReplyDetailsForm(Long reviewId, String content, LocalDateTime createdTime) {
        this.reviewId = reviewId;
        this.content = content;
        this.createdTime = createdTime;
    }
}
