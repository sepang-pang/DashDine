package jpabook.dashdine.domain.restaurant;

import jakarta.persistence.*;
import jpabook.dashdine.domain.common.Address;
import jpabook.dashdine.domain.common.Timestamped;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.restaurant.UpdateRestaurantRequestDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "restaurant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 가게 이름
    @Column(nullable = false)
    private String name;

    // 가게 전화번호
    @Column(nullable = false)
    private String tel;

    // 가게 소개
    private String info;

    // 최소 주문 가격
    private int minimumPrice;

    // 영업 시작 시간
    private String openingTime;

    // 영업 마감 시간
    private String closingTime;

    // 영업 상태
    private boolean isOperating;

    // 폐점 여부
    private boolean isDeleted;

    // 페점 시간
    private LocalDateTime deletedAt;

    // 주소
    @Embedded
    @Column(nullable = false)
    private Address address;

    // 좌표 중심 값
//    @Column(nullable = false) 추후 프런트 단계에서 활성화
    private Point point;

    // 관계 매핑
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 메뉴
//    @JsonIgnore
//    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE)
//    List<Menu> menuList = new ArrayList<>();

    @Builder
    public Restaurant(String name,
                      String tel, String info, int minimumPrice,
                      String openingTime, String closingTime,
                      boolean isOperating, boolean isDeleted,
                      LocalDateTime deletedAt, Address address,
                      Point point, User user) {
        this.name = name;
        this.tel = tel;
        this.info = info;
        this.minimumPrice = minimumPrice;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.isOperating = isOperating;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.address = address;
        this.point = point;
        updateUser(user);
    }

    private void updateUser(User user) {
        if(this.user != null) {
            this.user.getRestaurants().remove(this);
        }

        this.user = user;

        if(!user.getRestaurants().contains(this)) {
            user.getRestaurants().add(this);
        }
    }

    public void update(UpdateRestaurantRequestDto updateRestaurantRequestDto) {
        if(updateRestaurantRequestDto.getName() != null) {
            this.name = updateRestaurantRequestDto.getName();
        }
        if(updateRestaurantRequestDto.getTel() != null) {
            this.tel = updateRestaurantRequestDto.getTel();
        }
        if(updateRestaurantRequestDto.getInfo() != null) {
            this.info = updateRestaurantRequestDto.getInfo();
        }
        if(updateRestaurantRequestDto.getOpeningTime() != null) {
            this.openingTime = updateRestaurantRequestDto.getOpeningTime();
        }
        if(updateRestaurantRequestDto.getClosingTime() != null) {
            this.closingTime = updateRestaurantRequestDto.getClosingTime();
        }
    }
}
