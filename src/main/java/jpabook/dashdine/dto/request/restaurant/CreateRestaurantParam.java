package jpabook.dashdine.dto.request.restaurant;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CreateRestaurantParam {
    // 가게 이름
    @NotEmpty(message = "가게 이름을 입력해주세요")
    public String name;

    // 가게 전화번호
    @NotEmpty(message = "가게 번호를 입력해주세요")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "유효하지 않은 전화번호 형식입니다")
    public String tel;

    // 가게 소개
    public String info;

    // 최소 주문 가격
    public int minimumPrice;

    // 영업 시작 시간
    public String openingTime;

    // 영업 마감 시간
    public String closingTime;

    // 카테고리
    public Long categoryId;

    public CreateRestaurantParam() {

    }

    public CreateRestaurantParam(String name, String tel) {
        this.name = name;
        this.tel = tel;
    }
}
