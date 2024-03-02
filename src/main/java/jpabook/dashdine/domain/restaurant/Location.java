package jpabook.dashdine.domain.restaurant;

import lombok.Getter;

@Getter
public class Location { // 추후 record 클래스로 변경 여지
    private Double latitude;
    private Double longitude;

    public Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
