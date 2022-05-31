package com.kloong.servlet.basic.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloong.servlet.basic.HelloData;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "responseJsonServlet", urlPatterns = "/response-json")
public class ResponseJsonServlet extends HttpServlet {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Content-Type: application/json
        response.setContentType("application/json");
//        response.setCharacterEncoding("utf-8");

        HelloData helloData = new HelloData();
        helloData.setUsername("Kloong");
        helloData.setAge(27);

        String result = objectMapper.writeValueAsString(helloData);
        byte[] bytes = objectMapper.writeValueAsBytes(helloData);
//        response.getWriter().write(result);
        response.getOutputStream().write(bytes);
    }
}
