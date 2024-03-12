package jpabook.dashdine.domain.cart;

import jakarta.persistence.*;
import jpabook.dashdine.domain.menu.Menu;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
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

    @Column(name = "isDeleted")
    public boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToMany(mappedBy = "cartMenu", cascade = PERSIST, orphanRemoval = true)
    private List<CartMenuOption> cartMenuOptions = new ArrayList<>();

    @Builder
    public CartMenu(int count, Cart cart, Menu menu) {
        this.count = count;
        this.cart = cart;
        this.menu = menu;
    }

    public void increaseCount(int addCount) {
        this.count += addCount;
    }

    public void updateCount(int count) {
        this.count = count;
    }
}
