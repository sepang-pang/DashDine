package jpabook.dashdine.dto.request.user;

import lombok.Getter;

@Getter
public class UserModificationParam {
    private String nickName;
    private String zipcode;
    private String address;
    private String addressDetail;
    private double longitude;
    private double latitude;
}
