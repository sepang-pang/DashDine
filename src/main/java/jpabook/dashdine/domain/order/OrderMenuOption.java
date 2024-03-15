package jpabook.dashdine.domain.order;

import jakarta.persistence.*;
import jpabook.dashdine.domain.menu.Option;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "order_menu_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenuOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int optionPrice;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_menu_id")
    private OrderMenu orderMenu;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "menu_option_id")
    private Option option;

    @Builder
    public OrderMenuOption(int optionPrice, OrderMenu orderMenu, Option option) {
        this.optionPrice = optionPrice;
        this.orderMenu = orderMenu;
        this.option = option;
    }

    //== 연관관계 메서드 ==//
    public void updateOrderMenu(OrderMenu orderMenu) {
        this.orderMenu = orderMenu;
        orderMenu.getOrderMenuOptions().add(this);
    }
}
