package jpabook.dashdine.domain.order;

import lombok.Getter;

@Getter
public enum OrderStatus {

    PENDING(Status.PENDING),  // 주문 접수 전
    RECEIVED(Status.RECEIVED), // 주문 접수 완료
    CANCEL(Status.CANCEL);  // 주문 취소

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public static class Status {
        public static final String PENDING = "ORDER_PENDING";
        public static final String RECEIVED = "ORDER_RECEIVED";
        public static final String CANCEL = "ORDER_CANCEL";

    }
}
