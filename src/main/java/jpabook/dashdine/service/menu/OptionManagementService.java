package jpabook.dashdine.service.menu;

import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.domain.menu.Option;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.menu.CreateOptionParam;
import jpabook.dashdine.repository.menu.OptionRepository;
import jpabook.dashdine.service.menu.query.MenuQueryService;
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

        if (!menu.getRestaurant().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        
        // 옵션 생성
        System.out.println("// ============== 옵션생성 ============== //");
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
        if (!findOption.getMenu().getRestaurant().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        // 제거
        optionRepository.delete(findOption);
    }


    // ========= 검증 메서드 ========= //
    private void existOptionContent(CreateOptionParam param) {
        List<String> optionContent = optionRepository.findOptionContent(param.getMenuId());
        if(optionContent.contains(param.getContent())) {
            throw new IllegalArgumentException("동일한 내용의 옵션이 존재합니다.");
        }
    }
}
