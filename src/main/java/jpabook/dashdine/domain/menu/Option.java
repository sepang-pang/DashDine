package jpabook.dashdine.domain.menu;

import jakarta.persistence.*;
import jpabook.dashdine.domain.restaurant.Restaurant;
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

    // 연간관계 편의 메서드
    private void updateMenu(Menu menu) {
        if(this.menu != null) {
            this.menu.getOptions().remove(this);
        }

        this.menu = menu;

        if(!menu.getOptions().contains(this)) {
            menu.getOptions().add(this);
        }
    }
}
