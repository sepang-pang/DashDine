package jpabook.dashdine.dto.request.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CancelOrderParam {
    private String cancelContent;
}
