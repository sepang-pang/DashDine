package jpabook.dashdine.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j(topic = "UserController")
@Controller
@RequiredArgsConstructor
public class UserController {
    @GetMapping("/login")
    public String login() {
        return "signin";
    }

}




