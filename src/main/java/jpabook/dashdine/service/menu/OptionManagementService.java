package jpabook.dashdine.service.menu;

import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.domain.menu.Option;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.menu.CreateOptionRequestDto;
import jpabook.dashdine.repository.menu.OptionRepository;
import jpabook.dashdine.service.menu.query.MenuQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OptionManagementService {

    private final OptionRepository optionRepository;
    private final MenuQueryService menuQueryService;

    // 옵션 생성
    public void createOption(User user, CreateOptionRequestDto createOptionRequestDto) {
        // 옵션 중복 검사
        System.out.println("// ============== 옵션 중복 검사 ============== //");
        existOptionContent(createOptionRequestDto);

        // 메뉴 조회
        System.out.println("// ============== 메뉴 조회 검사 ============== //");
        Menu menu = menuQueryService.findOneMenu(createOptionRequestDto.getMenuId());
        
        // 옵션 생성
        System.out.println("// ============== 옵션생성 ============== //");
        Option option = Option.builder()
                .content(createOptionRequestDto.getContent())
                .price(createOptionRequestDto.getPrice())
                .menu(menu)
                .build();

        // 옵션 저장
        optionRepository.save(option);
    }


    // ========= Private 메서드 ========= //
    private void existOptionContent(CreateOptionRequestDto createOptionRequestDto) {
        List<String> optionContent = optionRepository.findOptionContent(createOptionRequestDto.getMenuId());
        if(optionContent.contains(createOptionRequestDto.getContent())) {
            throw new IllegalArgumentException("동일한 내용의 옵션이 존재합니다.");
        }
    }

    // ========= Public 메서드 ========= //
    public List<Option> findOptions(List<Long> optionIds) {
        return optionRepository.findByIdIn(optionIds);
    }
}
