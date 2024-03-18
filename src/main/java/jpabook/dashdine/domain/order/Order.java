package jpabook.dashdine.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.order.CancelOrderParam;
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
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String cancelContent;

    private int totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = PERSIST, orphanRemoval = true)
    private List<OrderMenu> orderMenus = new ArrayList<>();

    @JsonIgnore
    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;


    @Builder
    public Order(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    //== 생성 메서드 ==//
    public static Order createOrder(User findUser, Delivery delivery, List<OrderMenu> orderMenu) {
        Order order = Order.builder()
                .orderStatus(OrderStatus.PENDING)
                .build();
        order.updateUser(findUser);
        order.updateDelivery(delivery);
        order.calculateTotalPrice(orderMenu);
        order.addOrderMenu(orderMenu);

        return order;
    }

    //== 연관관계 메서드 ==//
    public void updateUser(User user) {
        this.user = user;
        user.getOrders().add(this);
    }

    private void updateDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.updateOrder(this);
    }

    public void addOrderMenu(List<OrderMenu> orderMenus) {
        this.orderMenus.addAll(orderMenus);
        orderMenus.forEach(orderMenu -> orderMenu.updateOrder(this));
    }

    //== 산출 메서드 ==//
    public void calculateTotalPrice(List<OrderMenu> orderMenus) {
        for (OrderMenu orderMenu : orderMenus) {
            for (OrderMenuOption option : orderMenu.getOrderMenuOptions()) {
                this.totalPrice += option.getOptionPrice();
            }
            this.totalPrice += orderMenu.getOrderPrice();
        }
    }

    //== 주문 취소 메서드 ==//
    public void cancelOrder(CancelOrderParam param) {
        this.cancelContent = param.getCancelContent();
        this.deletedAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.CANCEL;
    }
}
