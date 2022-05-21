# HttpServletResponse

## 역할
##### HTTP 응답 메세지를 생성한다
HTTP 응답 메세지를 하나 하나 구성하는 것은 너무 귀찮은 일이기 때문에, HttpServletResponse는 응답 메세지를 편하게 만들 수 있는 여러 메서드를 제공한다.  

- HTTP 응답 코드 지정
- 헤더 생성
- 바디 생성

##### 편의 기능 제공
- Content-Type 헤더의 값을 편리하게 지정 가능
- 쿠키 관련 설정
- Redirect 관련 설정


## 기본 사용법
HttpServletResponse가 제공하는 메서드들을 사용해서 HTTP 응답 메시지를 쉽게 구성할 수 있다.

##### ResponseHeaderServlet.java
```Java
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
```

요청을 보내고 응답 메시지를 확인해보면,

![](스크린샷%202022-05-21%20오후%2010.58.35.png)

위와 같이 헤더가 잘 설정되어 있는 것을 볼 수 있다. Content-Length가 0인 이유는 redirection을 했기 때문이다. 실제로 redirection도 정상적으로 잘 된다. 쿠키도 잘 설정되었음을 확인할 수 있다. 

참고로 웹 브라우저에서 동일한 요청을 다시 보내면,

![](스크린샷%202022-05-21%20오후%2010.59.59.png)

쿠키를 읽어서 요청할 때 같이 보내는 것을 확인할 수 있다.
