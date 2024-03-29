package jpabook.dashdine.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jpabook.dashdine.domain.common.Timestamped;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.order.CancelOrderParam;
import lombok.*;
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
public class Order extends Timestamped {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String cancelContent;

    private int totalPrice;

    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

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
    public static Order createOrder(User findUser, Delivery delivery, List<OrderMenu> orderMenu, int minimumPrice) {
        Order order = Order.builder()
                .orderStatus(OrderStatus.PENDING)
                .build();
        order.updateUser(findUser);
        order.updateDeliveryTime(delivery);
        order.calculateTotalPrice(orderMenu, minimumPrice);
        order.addOrderMenu(orderMenu);

        return order;
    }

    //== 연관관계 메서드 ==//
    public void updateUser(User user) {
        this.user = user;
        user.getOrders().add(this);
    }

    private void updateDeliveryTime(Delivery delivery) {
        this.delivery = delivery;
        delivery.updateOrder(this);
    }

    public void addOrderMenu(List<OrderMenu> orderMenus) {
        this.orderMenus.addAll(orderMenus);
        orderMenus.forEach(orderMenu -> orderMenu.updateOrder(this));
    }

    //== 산출 메서드 ==//
    public void calculateTotalPrice(List<OrderMenu> orderMenus, int minimumPrice) {
        for (OrderMenu orderMenu : orderMenus) {
            for (OrderMenuOption option : orderMenu.getOrderMenuOptions()) {
                this.totalPrice += option.getOptionPrice();
            }
            this.totalPrice += orderMenu.getOrderPrice();
        }

        if (this.totalPrice < minimumPrice) {
            throw new IllegalArgumentException("최소 주문 금액은 " + minimumPrice + " 원 입니다.");
        }
    }

    //== 주문 취소 메서드 ==//
    public void cancelOrder(CancelOrderParam param) {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new IllegalArgumentException("접수 완료가 되었거나, 이미 취소가 된 상품입니다.");
        }
        this.cancelContent = param.getCancelContent();
        this.orderStatus = OrderStatus.CANCEL;
        updateDeletedAt(LocalDateTime.now());
    }

    //== 주문 접수 메서드 ==//
    public void receiveOrder(int estimateTime) {
        this.orderStatus = OrderStatus.RECEIVED;
        this.delivery.updateEstimateTime(estimateTime);
    }

    //== 주문 내역 삭제 메서드 ==//
    public void deleteOrder() {
        if (this.isDeleted) {
            throw new IllegalArgumentException("이미 삭제된 주문 내역입니다.");
        }
        this.isDeleted = true;
        updateDeletedAt(LocalDateTime.now());
    }
}
