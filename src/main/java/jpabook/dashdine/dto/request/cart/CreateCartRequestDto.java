package jpabook.dashdine.dto.request.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class CreateCartRequestDto {
    private Long menuId;
    private int count;
    private Set<Long> options;
}
