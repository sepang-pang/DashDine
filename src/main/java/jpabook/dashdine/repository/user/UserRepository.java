package jpabook.dashdine.repository.user;

import jpabook.dashdine.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);
    Optional<User> findByEmail(String email);
    @Query("select u from User u where u.deletedAt < :threshold and u.deletedAt is not null ")
    List<User> findUsersToDelete(LocalDateTime threshold);
}
