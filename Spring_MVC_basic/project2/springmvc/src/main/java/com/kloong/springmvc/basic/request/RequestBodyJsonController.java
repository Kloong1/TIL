package com.kloong.springmvc.basic.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloong.springmvc.basic.HelloData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * {"username":"hello", "age":20}
 * content-type: application/json
 */
@Slf4j
@Controller
public class RequestBodyJsonController {

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/request-body-json-v1")
    public void requestBodyJsonVer1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody = {}", messageBody);
        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        log.info("helloData = {}", helloData);

        response.getWriter().write("OK");
    }

    @ResponseBody
    @PostMapping("/request-body-json-v2")
    public String requestBodyJsonVer2(@RequestBody String messageBody) throws IOException {
        log.info("messageBody = {}", messageBody);
        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        log.info("helloData = {}", helloData);

        return "OK";
    }

    @ResponseBody
    @PostMapping("/request-body-json-v3")
    public String requestBodyJsonVer3(@RequestBody HelloData helloData) throws IOException {
        log.info("helloData = {}", helloData);
        return "OK";
    }

    @ResponseBody
    @PostMapping("/request-body-json-v4")
    public String requestBodyJsonVer4(HttpEntity<HelloData> helloData) throws IOException {
        log.info("helloData = {}", helloData.getBody());
        return "OK";
    }

    @ResponseBody
    @PostMapping("/request-body-json-v5")
    public HelloData requestBodyJsonVer5(@RequestBody HelloData helloData) throws IOException {
        log.info("helloData = {}", helloData);
        return helloData;
    }

    @ResponseBody
    @PostMapping("/request-body-json-v6")
    public HttpEntity<HelloData> requestBodyJsonVer6(@RequestBody HelloData helloData) throws IOException {
        log.info("helloData = {}", helloData);
        return new HttpEntity<>(helloData);
    }
}
