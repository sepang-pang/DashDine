package jpabook.dashdine.dto.request.menu;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateOptionParam {
    private Long menuId;
    private String content;
    private int price;
}
