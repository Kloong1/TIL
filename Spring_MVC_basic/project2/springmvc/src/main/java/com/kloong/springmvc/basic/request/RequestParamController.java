package com.kloong.springmvc.basic.request;

import com.kloong.springmvc.basic.HelloData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Controller
public class RequestParamController {

    @RequestMapping("/request-param-v1")
    public void requestParamVer1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        log.info("username = {}, age = {}", username, age);
        response.getWriter().write("OK");
    }

    @ResponseBody //return 값으로 view를 찾지 않고 응답 메시지 바디에 그대로 넣는다.
    @RequestMapping("/request-param-v2")
    public String requestParamVer2(@RequestParam String username,
                                   @RequestParam int age) {
        log.info("username = {}, age = {}", username, age);
        return "OK";
    }

    @ResponseBody
    @RequestMapping("/request-param-v4")
    public String requestParamVer4(String username, int age) {
        log.info("username = {}, age = {}", username, age);
        return "OK";
    }

    @ResponseBody
    @RequestMapping("/request-param-required")
    public String requestParamRequired(
            @RequestParam(required = true) String username,
            @RequestParam(required = false) Integer age) {
        log.info("username = {}, age = {}", username, age);
        return "OK";
    }

    @ResponseBody
    @RequestMapping("/request-param-default")
    public String requestParamDefault(
            @RequestParam(required = true, defaultValue = "guest") String username,
            @RequestParam(required = false, defaultValue = "-1") int age) {
        log.info("username = {}, age = {}", username, age);
        return "OK";
    }

    @ResponseBody
    @RequestMapping("/request-param-Map")
    public String requestParamMap(@RequestParam Map<String, Object> paramMap) {
        log.info("username = {}, age = {}", paramMap.get("username"), paramMap.get("age"));
        return "OK";
    }

    @ResponseBody
    @RequestMapping("/model-attribute-v1")
    public String modelAttributeVer1(@ModelAttribute HelloData helloData) {
        log.info("helloData = {}", helloData);
        return "OK";
    }

    @ResponseBody
    @RequestMapping("/model-attribute-v2")
    public String modelAttributeVer2(HelloData helloData) {
        log.info("helloData = {}", helloData);
        return "OK";
    }
}
