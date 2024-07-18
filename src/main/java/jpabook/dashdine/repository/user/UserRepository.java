package jpabook.dashdine.repository.user;

import jpabook.dashdine.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("select u from User u where u.isDeleted = false and u.id = :userId")
    Optional<User> findByUserId(@Param("userId") Long userId);

    @Query("select u from User u where u.deletedAt < :threshold and u.deletedAt is not null ")
    List<User> findUsersToDelete(LocalDateTime threshold);
}
