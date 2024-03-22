package jpabook.dashdine.service.menu.query;

import jpabook.dashdine.domain.menu.Option;
import jpabook.dashdine.dto.response.menu.MenuDetailsForm;
import jpabook.dashdine.dto.response.menu.OptionForm;
import jpabook.dashdine.repository.menu.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionQueryService {

    private final OptionRepository optionRepository;

    public List<Option> findOptions(List<Long> optionIds) {
        return optionRepository.findByIdIn(optionIds);
    }

    public List<OptionForm> findAllOptionForms(MenuDetailsForm menu) {
        return optionRepository.findOptionFormsByMenuId(menu.getMenuId());
    }

    public List<OptionForm> findAllOptionForms(List<Long> menuIds) {
        return optionRepository.findOptionFormsByMenuIdIn(menuIds);
    }
}
