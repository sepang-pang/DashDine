package jpabook.dashdine.domain.user;

import jakarta.persistence.*;
import jpabook.dashdine.domain.cart.Cart;
import jpabook.dashdine.domain.comment.Reply;
import jpabook.dashdine.domain.comment.Review;
import jpabook.dashdine.domain.common.Address;
import jpabook.dashdine.domain.common.Timestamped;
import jpabook.dashdine.domain.order.Order;
import jpabook.dashdine.domain.restaurant.Restaurant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nick_name", nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    private boolean isDeleted;

    @Embedded
    @Column(name = "adress")
    private Address address;

    @Column(name = "point", columnDefinition = "GEOMETRY")
    private Point point;

    @OneToMany(mappedBy = "user", cascade = REMOVE)
    private List<Restaurant> restaurants = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = REMOVE)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = REMOVE)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = REMOVE)
    private List<Reply> replies = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Builder
    public User(String nickName, String username, String email,
                UserRoleEnum role, String provider, String providerId,
                String city, String street, String zipcode, Point point) {
        this.nickName = nickName;
        this.username = username;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.address = new Address(city, street, zipcode);
        this.point = point;
    }

    public void deactivateUser() {
        updateUserStatus(LocalDateTime.now(), true);
    }

    public void recoverUser() {
        updateUserStatus(null, false);
    }

    public void createCart(Cart cart) {
        this.cart = cart;
    }

    private void updateUserStatus(LocalDateTime deletionTime, boolean deletedStatus) {
        updateDeletedAt(deletionTime);
        this.isDeleted = deletedStatus;
    }
}
