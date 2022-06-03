package com.kloong.springmvc.basic.response;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ResponseViewController {

    @RequestMapping("/response-view-v1")
    public ModelAndView responseViewVer1() {
        ModelAndView mav = new ModelAndView("/response/hello");
        mav.addObject("data", "hello v1!");
        return mav;
    }

    @RequestMapping("/response-view-v2")
    public String responseViewVer2(Model model) {
        model.addAttribute("data", "hello v2!");
        return "/response/hello";
    }

    @RequestMapping("/response/hello")
    public void responseViewVer3(Model model) {
        model.addAttribute("data", "hello v3!");
    }
}
