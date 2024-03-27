package jpabook.dashdine.dto.response.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewForm {
    private String username;
    private String content;
    private List<String> menuNames;

    public ReviewForm(String username, String content, List<String> menuNames) {
        this.username = username;
        this.content = content;
        this.menuNames = menuNames;

    }
}
