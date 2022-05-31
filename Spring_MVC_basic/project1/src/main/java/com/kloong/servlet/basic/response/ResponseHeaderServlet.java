package com.kloong.servlet.basic.response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "responseHeaderServlet", urlPatterns = "/response-header")
public class ResponseHeaderServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //[status-line]
        response.setStatus(HttpServletResponse.SC_OK);

        //[response-header]
        response.setHeader("Content-Type", "text/plain;charset=utf-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("my-header", "hello");

        //[header 편의 메서드]
        setContent(response);
        setCookie(response);
        setRedirect(response);

        //[response-body]
        response.getWriter().write("OK");
    }

    //content 관련 헤더 설정을 위한 편의 메서드
    private void setContent(HttpServletResponse response) {
        //response.setHeader("Content-Type", "text/plain;charset=utf-8") 와 동일하다.

        //Content-Type: text/plain;charset=utf-8
        //Content-Length: 2
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        //response.setContentLength(2); //자동 생성되므로 보통 생략함
    }

    //Cookie 관련 헤더 설정을 위한 편의 메서드
    private void setCookie(HttpServletResponse response) {
        //response.setHeader("Set-Cookie", "myCookie=good; Max-Age=600") 와 동일하다.

        //Set-Cookie: myCookie=good; Max-Age=600;
        Cookie cookie = new Cookie("myCookie", "good");
        cookie.setMaxAge(600); //600초
        response.addCookie(cookie);
    }

    //Redirect 관련 헤더 설정을 위한 편의 메서드
    private void setRedirect(HttpServletResponse response) throws IOException{
        //response.setStatus(HttpServletResponse.SC_FOUND);
        //response.setHeader("Location", "/basic/hello-form.html"); 와 동일하다.

        //Status Code 302
        //Location: /basic/hello-form.html
        //setRedirect()만 하면 상태 코드가 자동으로 302 같은 redirect에 대한 상태코드로 바뀐다
        response.sendRedirect("/basic/hello-form.html");
    }
}
