package jpabook.dashdine.service;

import jpabook.dashdine.domain.user.PasswordManager;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.repository.PasswordManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PasswordManagerService {

    private final PasswordManagerRepository passwordManagerRepository;

    public void save(User user, String password) {
        PasswordManager passwordManager = new PasswordManager(user, password);
        passwordManagerRepository.save(passwordManager);
    }

    @Transactional(readOnly = true)
    public List<PasswordManager> findPasswordHistory(User user) {
        return passwordManagerRepository.findTop3ByUserOrderByCreatedAtDesc(user);
    }
}
