package jpabook.dashdine.repository.order;

import jpabook.dashdine.domain.order.Order;

import java.util.List;

public interface OrderRepositoryCustom {

    /*
    유저의 전체 주문내역 조회

    delivery 와 fetch join
    주문 생성일 기준 내림차순 정렬 -> 최신순으로 조회
    */
    List<Order> findAllOrdersWithDelivery(Long userId);

}
