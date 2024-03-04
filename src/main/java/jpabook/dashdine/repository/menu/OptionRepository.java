package jpabook.dashdine.repository.menu;

import jpabook.dashdine.domain.menu.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {

    @Query("select o.content from Option o where o.menu.id = :menuId")
    List<String> findOptionContent(@Param("menuId")Long menuId);

}
