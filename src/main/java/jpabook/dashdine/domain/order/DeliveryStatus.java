package jpabook.dashdine.domain.order;

import lombok.Getter;

@Getter
public enum DeliveryStatus {

    PENDING(Delivery.PENDING),  // 준비 중
    SHIPPED(Delivery.SHIPPED),  // 배송 중
    DELIVERED(Delivery.DELIVERED); // 배송 완료


    private final String delivery;

    DeliveryStatus(String delivery) {
        this.delivery = delivery;
    }

    public static class Delivery {
        public static final String PENDING = "STATUS_PENDING";
        public static final String SHIPPED = "STATUS_SHIPPED";
        public static final String DELIVERED = "STATUS_DELIVERED";

    }
}
