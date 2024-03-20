package jpabook.dashdine.dto.request.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AddCartParam {
    private Long restaurantId;
    private Long menuId;
    private int count;
    private List<Long> options;
}
