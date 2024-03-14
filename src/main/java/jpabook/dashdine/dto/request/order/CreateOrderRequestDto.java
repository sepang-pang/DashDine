package jpabook.dashdine.dto.request.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateOrderRequestDto {
    private List<Long> cartMenuIds;
}
