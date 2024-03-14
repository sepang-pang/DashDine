package jpabook.dashdine.domain.order;

import jakarta.persistence.*;
import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.menu.Menu;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "order_menu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    private Menu menu;

    @ManyToOne(fetch = LAZY)
    private Order order;

    private int orderPrice;
    private int count;

    @Builder
    public OrderMenu(Long id, Menu menu, Order order, int orderPrice, int count) {
        this.id = id;
        this.menu = menu;
        this.order = order;
        this.orderPrice = orderPrice;
        this.count = count;
    }

    public void updateOrder(Order order) {
        this.order = order;
    }

    public static List<OrderMenu> createOrderItem(List<CartMenu> cartMenus) {
        return cartMenus.stream()
                .map(cartMenu -> OrderMenu.builder()
                        .menu(cartMenu.getMenu())
                        .orderPrice(cartMenu.getMenu().getPrice())
                        .count(cartMenu.getCount())
                        .build()
                ).collect(Collectors.toList());
    }
}
