package jpabook.dashdine.repository.menu;

import jpabook.dashdine.domain.menu.Option;
import jpabook.dashdine.dto.response.menu.ReadOptionResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface OptionRepository extends JpaRepository<Option, Long> {


    List<Option> findByIdIn(List<Long> optionId);

    @Query("select o.content from Option o where o.menu.id = :menuId")
    List<String> findOptionContent(@Param("menuId")Long menuId);

    @Query("select new jpabook.dashdine.dto.response.menu.ReadOptionResponseDto (o.menu.id, o.content, o.price) from Option o join o.menu m where o.menu.id = :menuId")
    List<ReadOptionResponseDto> findOptionsByOneMenu(@Param("menuId") Long menuId);

    @Query("select new jpabook.dashdine.dto.response.menu.ReadOptionResponseDto (o.menu.id, o.content, o.price) from Option o join o.menu m where o.menu.id in :menuIds")
    List<ReadOptionResponseDto> findOptionsByMultipleMenus(@Param("menuIds") List<Long> menuId);

}
