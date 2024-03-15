package jpabook.dashdine.domain.order;

import lombok.Getter;

@Getter
public enum OrderStatus {

    ORDER(Status.ORDER),  // 주문 완료
    CANCEL(Status.CANCEL);  // 주문 취소

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public static class Status {
        public static final String ORDER = "ORDER_COMPLETED";
        public static final String CANCEL = "ORDER_CANCEL";

    }
}
