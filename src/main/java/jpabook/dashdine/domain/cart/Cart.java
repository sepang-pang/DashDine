package jpabook.dashdine.domain.cart;

import jakarta.persistence.*;
import jpabook.dashdine.domain.common.Timestamped;
import jpabook.dashdine.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "cart")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends Timestamped {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private boolean isDeleted;

    @OneToOne(mappedBy = "cart")
    private User user;

    public Cart(User user) {
        this.user = user;
    }
}
