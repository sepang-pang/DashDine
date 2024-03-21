package jpabook.dashdine.service.order.query;

import jpabook.dashdine.domain.order.OrderMenuOption;
import jpabook.dashdine.repository.order.OrderMenuOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderMenuOptionQueryService {

    private final OrderMenuOptionRepository orderMenuOptionRepository;

    public List<OrderMenuOption> findAllOrderOption(List<Long> menuIds) {
        List<OrderMenuOption> findOrderOptions = orderMenuOptionRepository.findOrderOptionsByOrderMenuIdIn(menuIds);

        if (findOrderOptions == null || findOrderOptions.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }

        return findOrderOptions;
    }
}
