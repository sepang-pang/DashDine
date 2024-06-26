package jpabook.dashdine.domain.comment;

import jakarta.persistence.*;
import jpabook.dashdine.domain.common.Timestamped;
import jpabook.dashdine.domain.order.DeliveryStatus;
import jpabook.dashdine.domain.order.Order;
import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.comment.CreateReviewParam;
import jpabook.dashdine.dto.request.comment.UpdateReviewParam;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends Timestamped {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "image")
    private String image;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @JoinColumn(name = "order_id")
    @OneToOne(fetch = LAZY)
    private Order order;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = LAZY)
    private User user;

    @JoinColumn(name = "restaurant_id")
    @ManyToOne(fetch = LAZY)
    private Restaurant restaurant;

    @OneToOne(mappedBy = "review", cascade = CascadeType.REMOVE)
    private Reply reply;

    @Builder
    public Review(String content, String image, Order order, User user, Restaurant restaurant) {
        this.content = content;
        this.image = image;
        this.order = order;
        updateUser(user);
        updateRestaurant(restaurant);
    }

    // == 생성 메서드 == //
    public static Review createReview(User user, Order order, CreateReviewParam param) {

        if (!user.getId().equals(order.getUser().getId())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        if (!order.getDelivery().getDeliveryStatus().equals(DeliveryStatus.DELIVERED)) {
            throw new IllegalArgumentException("배달이 완료된 주문에 대해서만 리뷰를 작성할 수 있습니다.");
        }

        return Review.builder()
                .content(param.getContent())
                .image(param.getImage())
                .order(order)
                .user(user)
                .restaurant(order.getOrderMenus().get(0).getMenu().getRestaurant())
                .build();
    }

    // == 연관관계 메서드 == //
    private void updateUser(User user) {
        this.user = user;
        user.getReviews().add(this);
    }

    private void updateRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        restaurant.getReviews().add(this);
    }

    public void updateReview(User user, UpdateReviewParam param) {
        validateUser(user);

        if (param.getContent() != null) {
            this.content = param.getContent();
        }

        if (param.getImage() != null) {
            this.image = param.getImage();
        }
    }

    public void deleteReview(User user) {
        validateUser(user);
        this.isDeleted = true;
        updateDeletedAt(LocalDateTime.now());
    }


    // == 검증 메서드 == //
    private void validateUser(User user) {
        if (!this.user.getId().equals(user.getId())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
    }
}
