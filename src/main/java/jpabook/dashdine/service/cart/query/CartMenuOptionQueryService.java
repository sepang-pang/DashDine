package jpabook.dashdine.service.cart.query;

import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.cart.CartMenuOption;
import jpabook.dashdine.repository.cart.CartMenuOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartMenuOptionQueryService {

    private final CartMenuOptionRepository cartMenuOptionRepository;

    // 복수의 메뉴 Id를 통해 CartMenuOption 조회
    public List<CartMenuOption> findCartOptionsByIds(List<Long> cartMenuIds) {
        return cartMenuOptionRepository.findCartMenuOptionByMenuIds(cartMenuIds);
    }

    // 복수의 CartMenuOption 을 저장
    public void saveAllCartMenuOption(List<CartMenuOption> cartMenuOptions) {
        cartMenuOptionRepository.saveAll(cartMenuOptions);
    }

    // 복수의 CartMenuOption 삭제
    public void deleteAllCartMenuOptions(List<CartMenu> cartMenus) {
        cartMenuOptionRepository.deleteAllByCartMenu(cartMenus);
    }
}
