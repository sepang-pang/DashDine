package jpabook.dashdine.service.menu;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.menu.CreateOptionParam;

public interface OptionService {

    void createOption(User user, CreateOptionParam param);

    void deleteOption(User user, Long optionId);
}
