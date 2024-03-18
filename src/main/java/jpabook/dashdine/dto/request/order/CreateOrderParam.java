package jpabook.dashdine.dto.request.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateOrderParam {
    private List<Long> cartMenuIds;
}
