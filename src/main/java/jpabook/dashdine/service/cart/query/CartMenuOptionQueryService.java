package jpabook.dashdine.service.cart.query;

import jpabook.dashdine.domain.cart.CartMenuOption;
import jpabook.dashdine.repository.cart.CartMenuOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartMenuOptionQueryService {

    private final CartMenuOptionRepository cartMenuOptionRepository;

    // 복수의 메뉴 Id를 통해 CartMenuOption 조회
    public Set<CartMenuOption> findCartOptionsByIds(List<Long> cartMenuIds) {
        return cartMenuOptionRepository.findCartMenuOptionByMenuIds(cartMenuIds);
    }

    public List<CartMenuOption> findCartOptionsByIdsToList(List<Long> cartMenuIds) {
        return cartMenuOptionRepository.findCartMenuOptionByMenuIdsToList(cartMenuIds);
    }

    // 복수의 CartMenuOption 을 저장
    @Transactional
    public void saveAllCartMenuOption(List<CartMenuOption> cartMenuOptions) {
        cartMenuOptionRepository.saveAll(cartMenuOptions);
    }
}
