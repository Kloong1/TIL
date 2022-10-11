package kloong.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {

    @RequestMapping("/")
    public String test() {
        return "index";
    }

    @RequestMapping("/ex")
    public void exception() {
        throw new RuntimeException();
    }
}
