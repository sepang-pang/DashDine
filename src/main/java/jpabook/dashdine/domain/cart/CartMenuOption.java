package jpabook.dashdine.domain.cart;

import jakarta.persistence.*;
import jpabook.dashdine.domain.menu.Option;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "cart_menu_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartMenuOption {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cart_menu_id")
    private CartMenu cartMenu;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "option_id")
    private Option option;

    @Builder
    public CartMenuOption(CartMenu cartMenu, Option option) {
        this.cartMenu = cartMenu;
        this.option = option;
    }
}
