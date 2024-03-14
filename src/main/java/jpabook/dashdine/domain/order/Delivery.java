package jpabook.dashdine.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jpabook.dashdine.domain.common.Address;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "delivery")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String cancelContent;

    private LocalDateTime arrivedAt;

    private LocalDateTime estimatedAt;

    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Embedded
    @Column(nullable = false)
    private Address address;

    @JsonIgnore
    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Order order;

    @Builder
    public Delivery(DeliveryStatus deliveryStatus, Address address) {
        this.deliveryStatus = deliveryStatus;
        this.address = address;
    }

    //== 연관관계 메서드 ==//
    public void updateOrder(Order order) {
        this.order = order;
    }
}
