package jpabook.dashdine.domain.order;

import lombok.Getter;

@Getter
public enum DeliveryStatus {

    PENDING(Status.PENDING),  // 준비 중
    SHIPPED(Status.SHIPPED),  // 배송 중
    DELIVERED(Status.DELIVERED); // 배송 완료


    private final String status;

    DeliveryStatus(String status) {
        this.status = status;
    }

    public static class Status {
        public static final String PENDING = "STATUS_PENDING";
        public static final String SHIPPED = "STATUS_SHIPPED";
        public static final String DELIVERED = "STATUS_DELIVERED";
    }
}
