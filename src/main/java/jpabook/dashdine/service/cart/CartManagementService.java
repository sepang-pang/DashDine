package jpabook.dashdine.service.cart;

import jpabook.dashdine.domain.cart.Cart;
import jpabook.dashdine.repository.cart.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Cart Management Service")
public class CartManagementService {

    private final CartRepository cartRepository;

    public void saveCart(Cart cart) {
        cartRepository.save(cart);
    }
}
