package jpabook.dashdine.dto.request.menu;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMenuParam {
    private Long restaurantId;
    private String name;
    private Integer price;
    private String content;
    private String image;
}
