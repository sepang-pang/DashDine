package jpabook.dashdine.dto.request.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
public class AddCartParam {
    private Long restaurantId;
    private Long menuId;
    private int count;
    private Set<Long> options = new HashSet<>();
}
