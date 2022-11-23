# HTTP 응답 데이터 - Plain Text, HTML, JSON(API 응답)

##### HTTP 응답 메시지에는 주로 다음과 같은 형식의 내용을 담아서 전달한다.
- 단순 텍스트 응답
	- 앞에서 살펴 봤음 (`response.getWriter().write("OK")`)
- HTML 응답
- HTTP API - JSON 등의 형태로 응답


## HTML 응답
##### ResponseHtmlServlet.java
```Java
package com.kloong.servlet.basic.response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "responseHtmlServlet", urlPatterns = "/response-html")
public class ResponseHtmlServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Content-Type : text/html;charset=utf-8
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        PrintWriter writer = response.getWriter();

        writer.println("<html>");
        writer.println("<body>");
        writer.println("    <div>Hello</div>");
        writer.println("</body>");
        writer.println("</html>");
    }
}
```
HTTP 응답으로 HTML을 반환할 때는 content-type을 `text/html` 로 지정해야 한다. 그래야 웹 브라우저가 정상적으로 렌더링 할 수 있다.


## API (JSON) 응답
##### ResponseJsonServlet.java
```Java
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
        response.setCharacterEncoding("utf-8");

        HelloData helloData = new HelloData();
        helloData.setUsername("Kloong");
        helloData.setAge(27);

        String result = objectMapper.writeValueAsString(helloData);
        response.getWriter().write(result);
    }
}
```
- HTTP 응답으로 JSON을 반환할 때는 content-type을 `application/json` 로 지정해야 한다.
- Jackson 라이브러리가 제공하는 `objectMapper.writeValueAsString()` 를 사용하면 객체를 JSON 형태의 string으로 파싱할 수 있다.

>**참고**
>`application/json` 은 스펙상 `utf-8` 형식을 사용하도록 정의되어 있다. 그래서 `Content-Type:application/json;charset=utf-8` 과 같이 character set을 임의로 지정해 주는 것은 의미 없는 파라미터를 추가한 것이 된다.
>그런데 `response.getWriter()`를 사용하면 character set을 지정하지 않아도 자동으로 default characer set 파라미터를 추가해버린다. 이때는 `response.getOutputStream()`으로 출력하면 그런 문제가 없다.

##### 참고 내용 코드
```Java
//생략
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        HelloData helloData = new HelloData();
        helloData.setUsername("Kloong");
        helloData.setAge(27);

        byte[] bytes = objectMapper.writeValueAsBytes(helloData);
        response.getOutputStream().write(bytes);
    }
}
```