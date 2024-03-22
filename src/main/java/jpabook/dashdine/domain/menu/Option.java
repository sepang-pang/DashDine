package jpabook.dashdine.domain.menu;

import jakarta.persistence.*;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.menu.CreateOptionParam;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "menu_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Option {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "price", nullable = false)
    private int price;

    @JoinColumn(name = "menu_id")
    @ManyToOne(fetch = LAZY)
    private Menu menu;

    @Builder
    public Option(String content, int price, Menu menu) {
        this.content = content;
        this.price = price;
        updateMenu(menu);
    }

    // == 생성 메서드 == //
    public static Option CreateOption(Menu menu, CreateOptionParam param) {
        return Option.builder()
                .content(param.getContent())
                .price(param.getPrice())
                .menu(menu)
                .build();
    }

    // == 연간관계 편의 메서드 == //
    private void updateMenu(Menu menu) {
        if(this.menu != null) {
            this.menu.getOptions().remove(this);
        }

        this.menu = menu;

        if(!menu.getOptions().contains(this)) {
            menu.getOptions().add(this);
        }
    }

    // == 검증 메서드 == //
    public void validateAccessRole(User user) {
        if (!this.getMenu().getRestaurant().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
    }
}
