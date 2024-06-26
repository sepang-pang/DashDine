package jpabook.dashdine.service.menu;

import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.domain.menu.Option;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.menu.CreateOptionParam;
import jpabook.dashdine.repository.menu.OptionRepository;
import jpabook.dashdine.service.menu.query.MenuQueryService;
import jpabook.dashdine.util.StringNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OptionManagementService implements OptionService{

    private final OptionRepository optionRepository;
    private final MenuQueryService menuQueryService;

    // 옵션 생성
    @Override
    public void createOption(User user, CreateOptionParam param) {
        // 옵션 중복 검사
        existOptionContent(param);

        // 메뉴 조회
        Menu menu = menuQueryService.findOneMenu(param.getMenuId());

        // 본인 검증
        menu.validateAccessRole(user);
        
        // 옵션 생성
        Option option = Option.CreateOption(menu, param);

        // 옵션 저장
        optionRepository.save(option);
    }

    // 옵션 제거
    @Override
    public void deleteOption(User user, Long optionId) {
        // 옵션 조회
        Option findOption = optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));

        // 본인 검증
        findOption.validateAccessRole(user);

        // 제거
        optionRepository.delete(findOption);
    }


    // ========= 검증 메서드 ========= //
    private void existOptionContent(CreateOptionParam param) {
        List<String> findOptionContents = optionRepository.findOptionContent(param.getMenuId());
        String normalizedRequestName = StringNormalizer.normalizeString(param.getContent());

        for (String optionContent : findOptionContents) {
            if (StringNormalizer.normalizeString(optionContent).equals(normalizedRequestName)) {
                throw new IllegalArgumentException("동일한 옵션이 존재합니다.");
            }
        }
    }
}
