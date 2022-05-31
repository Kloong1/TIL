package com.kloong.servlet.basic;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("HelloServlet.service");
        System.out.println("request = " + request);
        System.out.println("response = " + response);

        String username = request.getParameter("username"); //쿼리 파라미터를 가져온다
        System.out.println("username = " + username);

        response.setContentType("text/plain"); //Content-Type 헤더 설정
        response.setCharacterEncoding("utf-8"); //Content-Type 헤더 설정
        response.getWriter().write("Hello " + username); //응답 메시지 바디
    }
}
