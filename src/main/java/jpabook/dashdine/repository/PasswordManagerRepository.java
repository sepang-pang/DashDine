package jpabook.dashdine.repository;

import jpabook.dashdine.domain.user.PasswordManager;
import jpabook.dashdine.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordManagerRepository extends JpaRepository<PasswordManager, Long> {
    List<PasswordManager> findTop3ByUserOrderByCreatedAtDesc(User user);
}