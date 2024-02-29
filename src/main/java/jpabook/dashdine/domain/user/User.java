package jpabook.dashdine.domain.user;

import jakarta.persistence.*;
import jpabook.dashdine.domain.common.Timestamped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = ?")
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

    public User(String loginId, String password, String email, UserRoleEnum role) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

}
