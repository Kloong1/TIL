package com.kloong.springmvc.basic.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
public class RequestBodyStringController {

    @PostMapping("/request-body-string-v1")
    public void requestBodyStringVer1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String message = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("message = {}", message);
        response.getWriter().write("OK");
    }

    @PostMapping("/request-body-string-v2")
    public void requestBodyStringVer2(InputStream inputStream, Writer responseWriter) throws IOException {
        String message = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        log.info("message = {}", message);
        responseWriter.write("OK");
    }

    @PostMapping("/request-body-string-v3")
    public HttpEntity<String> requestBodyStringVer3(HttpEntity<String> httpEntity) throws IOException {
        log.info("message = {}", httpEntity.getBody());
        return new HttpEntity<>("OK");
    }

    @ResponseBody
    @PostMapping("/request-body-string-v4")
    public String requestBodyStringVer4(@RequestBody String messageBody) throws IOException {
        log.info("message = {}", messageBody);
        return "OK";
    }

}
