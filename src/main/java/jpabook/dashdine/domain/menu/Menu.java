package jpabook.dashdine.domain.menu;

import jakarta.persistence.*;
import jpabook.dashdine.domain.common.Timestamped;
import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.dto.request.menu.UpdateMenuParam;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "menu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends Timestamped {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "image")
    private String image;

    @Column(name = "stack_quantity", nullable = false)
    private int stackQuantity;

    @Column(name = "is_menu_status")
    private boolean isMenuStatus;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @JoinColumn(name = "restaurant_id")
    @ManyToOne(fetch = LAZY)
    private Restaurant restaurant;

    @OneToMany(mappedBy = "menu", cascade = PERSIST, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    @Builder
    public Menu(String name, int price, String content, String image, int stackQuantity, Restaurant restaurant) {
        this.name = name;
        this.price = price;
        this.content = content;
        this.image = image;
        this.stackQuantity = stackQuantity;
        updateRestaurant(restaurant);
    }


    // 연간관계 편의 메서드
    private void updateRestaurant(Restaurant restaurant) {
        if(this.restaurant != null) {
            this.restaurant.getMenuList().remove(this);
        }

        this.restaurant = restaurant;

        if(!restaurant.getMenuList().contains(this)) {
            restaurant.getMenuList().add(this);
        }
    }

    public void updateMenu(UpdateMenuParam param) {
        if (param.getName() != null) {
            this.name = param.getName();
        }

        if (param.getPrice() != null) {
            this.price = param.getPrice();
        }

        if (param.getContent() != null) {
            this.content = param.getContent();
        }

        if(param.getImage() != null) {
            this.image = param.getImage();
        }

        if (param.getStackQuantity() != null) {
            this.stackQuantity = param.getStackQuantity();
        }
    }

    public void deleteMenu() {
        this.isDeleted = true;
        updateDeletedAt(LocalDateTime.now());
    }
}
