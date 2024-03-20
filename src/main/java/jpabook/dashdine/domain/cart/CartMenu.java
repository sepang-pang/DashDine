package jpabook.dashdine.domain.cart;

import jakarta.persistence.*;
import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.domain.menu.Option;
import jpabook.dashdine.dto.request.cart.AddCartParam;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "cart_menu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartMenu {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "count", nullable = false)
    private int count;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToMany(mappedBy = "cartMenu", cascade = ALL, orphanRemoval = true)
    private List<CartMenuOption> cartMenuOptions = new ArrayList<>();

    @Builder
    public CartMenu(int count, Cart cart, Menu menu) {
        this.count = count;
        this.cart = cart;
        this.menu = menu;
    }

    // == 생성 메서드 == //
    public static CartMenu CreateCartMenu(Cart findCart, Menu findMenu, List<Option> findOptions, AddCartParam param) {
        CartMenu cartMenu = CartMenu.builder()
                .cart(findCart)
                .menu(findMenu)
                .count(param.getCount())
                .build();

        List<CartMenuOption> cartOptions = findOptions.stream()
                .map(option -> CartMenuOption.builder()
                        .cartMenu(cartMenu)
                        .option(option)
                        .build())
                .toList();

        cartMenu.addOptions(cartOptions);

        return cartMenu;
    }

    // == 연관관계 메서드 == //
    private void addOptions(List<CartMenuOption> cartOptions) {
        this.cartMenuOptions.addAll(cartOptions);
    }


    // == 연산 메서드 == //
    public void increaseCount(int addCount) {
        this.count += addCount;
    }

    public void updateCount(int count) {
        this.count = count;
    }
}
