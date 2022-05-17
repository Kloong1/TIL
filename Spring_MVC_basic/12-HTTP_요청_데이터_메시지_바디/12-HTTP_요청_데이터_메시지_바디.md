# HTTP 요청 데이터 - 메시지 바디 (API 방식)
HTTP 메시지 바디에 데이터를 직접 담아서 서버에 전달해보자.

#### 특징
- HTTP API에서 주로 사용된다.
- JSON, XML, TEXT 등의 데이터 형식을 주로 사용한다.
	- 일반적으로 JSON을 사용한다.
- POST, PUT, PATCH 메소드와 함께 사용 가능하다.

## Plain Text를 메시지 바디에 담아서 전달
먼저 가장 단순한 플레인 텍스트 메시지를 HTTP 메시지 바디에 담아서 전송하고, 읽어보자. HTTP 메시지 바디의 데이터를 `InputStream`을 사용해서 직접 읽을 수 있다.

##### RequestBodyStringServlet.java
```Java
package com.kloong.servlet.basic.request;

import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "requestBodyStringServlet", urlPatterns = "/request-body-string")
public class RequestBodyStringServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //메시지 바디를 읽을 수 있는 InputStream
        ServletInputStream inputStream = request.getInputStream();
        //InputStream의 내용을 String으로 변환해준다. Spring에서 제공함.
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8); 

        System.out.println("messageBody = " + messageBody);

        response.getWriter().write("OK");
    }
}
```
- HTTP 메시지 바디를 읽을 수 있는 InputStream을 `request.getInputStream()` 을 통해서 받아온다.
- Spring이 제공하는 `StreamUtils.copyToString()` 메소드를 활용해서 InputStream에서 메시지 바디를 읽어 String으로 변환한다.
	- InputStream은 byte 코드를 반환한다. byte 코드를 우리가 읽을 수 있는 문자 형태(String)로 변환하려면 Charset을 지정해주어야 한다. 여기서는 UTF_8 Charset을 지정해주었다.

Postman으로 테스트해보면 메시지 바디가 정상적으로 출력되는 것을 확인할 수 있다.


## JSON 포맷 데이터를 메시지 바디에 담아서 전달
이번에는 HTTP API에서 주로 사용하는 JSON 형식으로 데이터를 전달해보자.

#### JSON 형식 요청 예시
```text
POST http://localhost:8080/request-body-json
Host: localhost:8080
content-type: application/json

{"username": "hello", "age": 20}
```

실무에서는 JSON 형태의 데이터를 바로 사용하지 않고, JSON 데이터를 파싱해서 객체로 만든 뒤 사용한다. 따라서 데이터를 담을 객체를 하나 만들자.

##### HelloData.java
```Java
package com.kloong.servlet.basic;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HelloData {
    private String username;
    private int age;
}
```

이제 클라이언트가 메시지 바디에 JSON 데이터를 담아서 서버에 전달하면, 해당 데이터를 파싱해서 객체로 만드는 서블릿을 작성해보자.

##### ReqeustBodyJsonServlet.java
```Java
package com.kloong.servlet.basic.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloong.servlet.basic.HelloData;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "requestBodyJsonServlet", urlPatterns = "/request-body-json")
public class RequestBodyJsonServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        System.out.println("messageBody = " + messageBody);

        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        System.out.println("helloData.getUsername() = " + helloData.getUsername());
        System.out.println("helloData.getAge() = " + helloData.getAge());

        response.getWriter().write("OK");
    }
}

```

- Spring에서는 객체를 JSON으로, JSON을 객체로 파싱하는 기본 라이브러리로 Jackson을 사용한다.
- Jackson 라이브러리에서 제공하는 `ObjectMapper` 로 클라이언트가 메시지 바디에 담아 보낸 JSON 형태의 데이터를 파싱해서 HelloData 객체로 만들었다.

>**참고**
>JSON 결과를 파싱해서 사용할 수 있는 자바 객체로 변환하려면 Jackson, Gson 같은 JSON 변환라이브러리를 추가해서 사용해야 한다. 스프링 부트로 Spring MVC를 선택하면 기본으로 Jackson라이브러리( ObjectMapper)를 함께 제공한다.

