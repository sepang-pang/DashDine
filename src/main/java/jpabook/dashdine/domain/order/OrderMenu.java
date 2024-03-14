package jpabook.dashdine.domain.order;

import jakarta.persistence.*;
import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.menu.Menu;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "order_menu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int orderPrice;

    private int count;

    @ManyToOne(fetch = LAZY)
    private Menu menu;

    @ManyToOne(fetch = LAZY)
    private Order order;

    @OneToMany(mappedBy = "orderMenu", cascade = PERSIST, orphanRemoval = true)
    private List<OrderMenuOption> orderMenuOptions = new ArrayList<>();

    @Builder
    public OrderMenu(Long id, Menu menu, Order order, int orderPrice, int count) {
        this.id = id;
        this.menu = menu;
        this.order = order;
        this.orderPrice = orderPrice;
        this.count = count;
    }

    //== 연관관계 메서드 ==//
    public void updateOrder(Order order) {
        this.order = order;
    }


    //== 생성 메서드 ==//
    public static List<OrderMenu> createOrderItem(List<CartMenu> cartMenus) {
        if(cartMenus.isEmpty()) {
            throw new IllegalArgumentException("해당 항목이 존재하지 않습니다.");
        }

        List<OrderMenu> orderMenus = cartMenus.stream()
                .map(cartMenu -> {
                            OrderMenu orderMenu = OrderMenu.builder()
                                    .menu(cartMenu.getMenu())
                                    .count(cartMenu.getCount())
                                    .build();

                            System.out.println("// ======= 주문 목록 옵션 생성 ======= //");
                            List<OrderMenuOption> orderOptions = cartMenu.getCartMenuOptions().stream()
                                    .map(cartMenuOption -> {
                                                OrderMenuOption orderMenuOption = OrderMenuOption.builder()
                                                        .option(cartMenuOption.getOption())
                                                        .build();
                                                orderMenuOption.updateOrderMenu(orderMenu);
                                                return orderMenuOption;
                                            }
                                    )
                                    .collect(Collectors.toList());
                            orderMenu.getOrderMenuOptions().addAll(orderOptions);
                            return orderMenu;
                        }
                ).collect(Collectors.toList());

        return orderMenus;
    }

}