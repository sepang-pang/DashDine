package jpabook.dashdine.domain.order;

import jakarta.persistence.*;
import jpabook.dashdine.domain.user.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)
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
    public Order(boolean orderStatus) {
        this.orderStatus = orderStatus;
    }

    //== 연관관계 메서드 ==//
    public void updateUser(User user) {
        this.user = user;
        user.getOrders().add(this);
    }

    public void addOrderMenu(List<OrderMenu> orderMenus) {
        this.orderMenus.addAll(orderMenus);
        orderMenus.forEach(orderMenu -> orderMenu.updateOrder(this));
    }

    //== 생성 메서드 ==//
    public static Order createOrder(User findUser, List<OrderMenu> orderMenu) {
        Order order = Order.builder()
                .orderStatus(true)
                .build();
        order.updateUser(findUser);
        order.addOrderMenu(orderMenu);

        return order;
    }
}
