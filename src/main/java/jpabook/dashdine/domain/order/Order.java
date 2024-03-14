package jpabook.dashdine.domain.order;

import jakarta.persistence.*;
import jpabook.dashdine.domain.user.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private boolean isDeleted;

    private String cancelContent;

    private boolean orderStatus;

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = PERSIST, orphanRemoval = true)
    private List<OrderMenu> orderMenus = new ArrayList<>();

    @Builder
    public Order(Long id, boolean isDeleted, String cancelContent, boolean orderStatus, LocalDateTime createdAt, LocalDateTime deletedAt, User user, List<OrderMenu> orderMenus) {
        this.id = id;
        this.isDeleted = isDeleted;
        this.cancelContent = cancelContent;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.user = user;
        this.orderMenus = orderMenus;
    }

    public void addOrderMenu(List<OrderMenu> orderMenus) {
        this.orderMenus = orderMenus;
        orderMenus.forEach(orderMenu -> orderMenu.updateOrder(this));
    }

    //==생성 메서드==//
    public static Order createOrder(User findUser, List<OrderMenu> orderMenu) {
        Order order = Order.builder()
                .user(findUser)
                .orderStatus(true)
                .build();
        order.addOrderMenu(orderMenu);

        return order;
    }
}
