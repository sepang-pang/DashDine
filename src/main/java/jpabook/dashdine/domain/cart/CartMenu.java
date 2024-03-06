package jpabook.dashdine.domain.cart;

import jakarta.persistence.*;
import jpabook.dashdine.domain.menu.Menu;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Builder
    public CartMenu(int count, Cart cart, Menu menu) {
        this.count = count;
        this.cart = cart;
        this.menu = menu;
    }

    public void increaseCount(int addCount) {
        this.count += addCount;
    }
}
