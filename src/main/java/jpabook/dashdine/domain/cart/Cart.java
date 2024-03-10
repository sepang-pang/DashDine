package jpabook.dashdine.domain.cart;

import jakarta.persistence.*;
import jpabook.dashdine.domain.common.Timestamped;
import jpabook.dashdine.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "cart")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends Timestamped {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = REMOVE)
    private List<CartMenu> cartMenus = new ArrayList<>();

    @OneToOne(mappedBy = "cart")
    private User user;

    public Cart(User user) {
        this.user = user;
    }
}
