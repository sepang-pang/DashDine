package jpabook.dashdine.dto.request.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateCartRequestDto {
    private int count;
    private List<Long> options;
}
