package jpabook.dashdine.domain.user;

import jakarta.persistence.*;
import jpabook.dashdine.domain.common.Timestamped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<PasswordManager> passwordManagers = new ArrayList<>();

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
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = true;
    }
}
