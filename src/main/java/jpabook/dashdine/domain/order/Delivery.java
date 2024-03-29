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

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Embedded
    @Column(nullable = false)
    private Address address;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime arrivedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime estimatedAt;

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

    //== 예상시간 업데이트 메서드 ==//
    public void updateEstimateTime(int estimateTime) {
        this.estimatedAt = LocalDateTime.now().plusMinutes(estimateTime);
    }

    public void updateDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;

        if (deliveryStatus.equals(DeliveryStatus.DELIVERED)) {
            this.arrivedAt = LocalDateTime.now();
        }
    }
}
