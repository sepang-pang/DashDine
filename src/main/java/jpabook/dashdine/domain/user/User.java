package jpabook.dashdine.domain.user;

import jakarta.persistence.*;
import jpabook.dashdine.domain.cart.Cart;
import jpabook.dashdine.domain.common.Timestamped;
import jpabook.dashdine.domain.restaurant.Restaurant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    private boolean isDeleted;

    @OneToMany(mappedBy = "user", cascade = REMOVE)
    private List<PasswordManager> passwordManagers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Restaurant> restaurants = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = REMOVE)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public User(String loginId, String password, String email, UserRoleEnum role) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
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
