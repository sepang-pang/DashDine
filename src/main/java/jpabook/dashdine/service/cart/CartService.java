package jpabook.dashdine.service.cart;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.cart.AddCartParam;
import jpabook.dashdine.dto.request.cart.UpdateCartParam;
import jpabook.dashdine.dto.response.cart.CartForm;

public interface CartService {

    // == 장바구니 추가 == //
    void addCart(User user, AddCartParam param);

    // == 장바구니 조회 == //
    CartForm readAllCart(User user);

    // == 장바구니 수정 == //
    void updateCart(User user, Long cartMenuId, Long menuId, UpdateCartParam param);

    // == 장바구니 삭제 == //
    void deleteCart(User user, Long cartMenuId);
}
