package jpabook.dashdine.domain.restaurant;

import jakarta.persistence.*;
import jpabook.dashdine.domain.comment.Review;
import jpabook.dashdine.domain.common.Address;
import jpabook.dashdine.domain.common.Timestamped;
import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.restaurant.CreateRestaurantParam;
import jpabook.dashdine.dto.request.restaurant.UpdateRestaurantParam;
import jpabook.dashdine.geo.GeometryUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // 페점 일시
    private LocalDateTime deletedAt;

    // 주소
    @Embedded
    @Column(nullable = false)
    private Address address;

    // 좌표 중심 값
    @Column(nullable = false, columnDefinition = "GEOMETRY")
    private Point point;

    // 관계 매핑
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE)
    List<Menu> menus = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE)
    List<Review> reviews = new ArrayList<>();

    @Builder
    public Restaurant(String name,
                      String tel, String info, int minimumPrice,
                      String openingTime, String closingTime,
                      boolean isOperating, boolean isDeleted,
                      LocalDateTime deletedAt, String street,
                      String streetDetail, String zipcode,
                      Category category, Point point) {
        this.name = name;
        this.tel = tel;
        this.info = info;
        this.minimumPrice = minimumPrice;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.isOperating = isOperating;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.category = category;
        this.address = new Address(street, streetDetail, zipcode);
        this.point = point;
    }

    public static Restaurant createRestaurant(User findUser, CreateRestaurantParam param, Category category) throws ParseException {
        Restaurant restaurant = Restaurant.builder()
                .name(param.getName())
                .info(param.getInfo())
                .tel(param.getTel())
                .minimumPrice(param.getMinimumPrice())
                .openingTime(param.getOpeningTime())
                .closingTime(param.getClosingTime())
                .category(category)
                .street(param.getStreet())
                .streetDetail(param.getStreetDetail())
                .zipcode(param.getZipcode())
                .point(GeometryUtil.calculatePoint(param.getLongitude(), param.getLatitude()))
                .build();
        restaurant.updateUser(findUser);

        return restaurant;
    }

    // 연관관계 편의 메서드
    private void updateUser(User user) {
        if (this.user != null) {
            this.user.getRestaurants().remove(this);
        }

        this.user = user;

        if (!user.getRestaurants().contains(this)) {
            user.getRestaurants().add(this);
        }
    }

    // 가게 수정 메서드
    public void updateRestaurant(UpdateRestaurantParam param, Category category) throws ParseException {
        if (param.getName() != null && !this.name.equals(param.getName())) {
            this.name = param.getName();
        }

        if (param.getTel() != null && !this.tel.equals(param.getTel())) {
            this.tel = param.getTel();
        }

        if (param.getInfo() != null && !this.info.equals(param.getInfo())) {
            this.info = param.getInfo();
        }

        if (param.getMinimumPrice() != this.minimumPrice) {
            this.minimumPrice = param.getMinimumPrice();
        }

        if (param.getOpeningTime() != null && !this.openingTime.equals(param.getOpeningTime())) {
            this.openingTime = param.getOpeningTime();
        }

        if (param.getClosingTime() != null && !this.closingTime.equals(param.getClosingTime())) {
            this.closingTime = param.getClosingTime();
        }

        if (param.getCategoryId() != null && (!this.category.getId().equals(param.getCategoryId()))) {
            this.category = category;
        }

        if (param.getStreet() != null && !this.address.getStreet().equals(param.getStreet())) {
            this.address.updateStreet(param.getStreet());
        }

        if (param.getStreetDetail() != null && !this.address.getStreetDetail().equals(param.getStreetDetail())) {
            this.address.updateDetail(param.getStreetDetail());
        }

        if (param.getZipcode() != null && !this.address.getZipcode().equals(param.getZipcode())) {
            this.address.updateZipcode(param.getZipcode());
        }

        if ((param.getLongitude() != 0 && param.getLatitude() != 0) && (param.getLongitude() != this.point.getX() || param.getLatitude() != this.point.getY())) {
            this.point = GeometryUtil.calculatePoint(param.getLongitude(), param.getLatitude());
        }

    }



    // 가게 논리 삭제 메서드
    public void deleteRestaurant() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
