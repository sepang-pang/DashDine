package jpabook.dashdine.domain.comment;

import jakarta.persistence.*;
import jpabook.dashdine.domain.common.Timestamped;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.comment.CreateReplyParam;
import jpabook.dashdine.dto.request.comment.UpdateReplyParam;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "reply")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply extends Timestamped {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Builder
    public Reply(String content, User user, Review review) {
        this.content = content;
        updateUser(user);
        this.review = review;
    }

    // == 생성 메서드 == //
    public static Reply createReply(User user, Review findReview, CreateReplyParam param) {
        if (!user.getRestaurants().contains(findReview.getRestaurant())) {
            throw new IllegalArgumentException("본인 소유의 가게에만 답글을 남길 수 있습니다.");
        }

        return Reply.builder()
                .content(param.getContent())
                .user(user)
                .review(findReview)
                .build();
    }

    // == 연관관계 메서드 == //
    private void updateUser(User user) {
        this.user = user;
        user.getReplies().add(this);
    }

    // == 수정 메서드 == //
    public void updateReply(User user, UpdateReplyParam param) {
        validateUser(user);

        if (param.getContent() != null) {
            this.content = param.getContent();
        }
    }

    // == 삭제 메서드 == //
    public void deleteReply(User user) {
        validateUser(user);
        this.isDeleted = false;
        updateDeletedAt(LocalDateTime.now());
    }

    // 검증 메서드
    private void validateUser(User user) {
        if (!this.user.getId().equals(user.getId())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
    }
}
