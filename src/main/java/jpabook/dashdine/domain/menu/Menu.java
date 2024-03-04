package jpabook.dashdine.domain.menu;

import jakarta.persistence.*;
import jpabook.dashdine.domain.common.Timestamped;
import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.dto.request.menu.UpdateMenuRequestDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public void update(UpdateMenuRequestDto updateMenuRequestDto) {
        if (updateMenuRequestDto.getName() != null) {
            this.name = updateMenuRequestDto.getName();
        }

        if (updateMenuRequestDto.getPrice() != null) {
            this.price = updateMenuRequestDto.getPrice();
        }

        if (updateMenuRequestDto.getContent() != null) {
            this.content = updateMenuRequestDto.getContent();
        }

        if(updateMenuRequestDto.getImage() != null) {
            this.image = updateMenuRequestDto.getImage();
        }

        if (updateMenuRequestDto.getStackQuantity() != null) {
            this.stackQuantity = updateMenuRequestDto.getStackQuantity();
        }
    }

    public void delete() {
        this.isDeleted = true;
        updateDeletedAt(LocalDateTime.now());
    }
}
