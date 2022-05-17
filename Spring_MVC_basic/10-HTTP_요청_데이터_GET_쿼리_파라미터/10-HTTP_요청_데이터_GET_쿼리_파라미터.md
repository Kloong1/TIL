# HTTP 요청 데이터 - GET & 쿼리 파라미터

다음 데이터를 클라이언트에서 서버로 전송해보자.
- username=hello
- age=20

이 데이터를 메시지 바디 없이, URL의 쿼리 파라미터를 사용해서 데이터를 전달하자. 쿼리 파라미터 방식은 검색, 필터, 페이징등에서 많이 사용한다.

쿼리 파라미터는 URL에 다음과 같이 ? 를 시작으로 보낼 수 있다. 추가 파라미터는 & 로 구분하면 된다.  
`http://localhost:8080/request-param?username=hello&age=20`

서버에서는 HttpServletRequest 가 제공하는 다음 메서드를 통해 쿼리 파라미터를 편리하게 조회할 수 있다.

## HttpServletRequest로 쿼리 파라미터 조회
```Java
package com.kloong.servlet.basic.request;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * 1. 쿼리 파라미터 조회 기능
 * http://localhost:8080/request-param?username=hello&age=20 요청에 대한 파라미터 조회
 * 2. 동일한 이름의 쿼리 파라미터 조회
 * username 파라미터가 여러개 들어온 요청에 대해서 파라미터 조회
 * http://localhost:8080/request-param?username=hello&username=kim&age=20
 */
@WebServlet(name = "requestParamServlet", urlPatterns = "/request-param")
public class RequestParamServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[전체 파라미터 조회] - start");

        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> System.out.println(paramName + " = " + request.getParameter(paramName)));

        System.out.println("[전체 파라미터 조회] - end");
        System.out.println();

        System.out.println("[단일 파라미터 조회] - start");
        String username = request.getParameter("username");
        String age = request.getParameter("age");

        System.out.println("username = " + username);
        System.out.println("age = " + age);

        System.out.println("[단일 파라미터 조회] - end");
        System.out.println();

        System.out.println("[이름이 같은 복수 파라미터 조회] - start");
        String[] usernames = request.getParameterValues("username");
        for (String uname : usernames)
            System.out.println("username = " + uname);

        System.out.println("[이름이 같은 복수 파라미터 조회] - end");

        response.getWriter().write("OK");
    }
}
```
- `request.getParameterNames()` : 모든 쿼리 파라미터의 이름 조회
- `request.getParameter()` : 특정 쿼리 파라미터의 값 조회
- `request.getParameterValues()` : 이름이 같은 파라미터가 여러개 존재하는 경우, 해당 파라미터의 모든 값 조회

#### 복수 파라미터에서 단일 파라미터 조회
`<URL>?username=hello&username=kim` 과 같이 동일한 이름을 가진 파라미터가 여러개 존재할 수도 있다. 이런 경우에는 `request.getParameter()` 를 사용해선 안된다. `request.getParameter()` 는 하나의 파라미터 이름에 대해서 하나의 값만 있을 때 사용해야 한다.
위의 상황처럼 동일한 이름의 파라미터가 여러개 존재할 때는 `request.getParameterValues()` 를 사용해서 해당 파라미터의 값을 전부 조회할 수 있다.

>참고: 이렇게 동일한 이름을 가진 파라미터가 여러개 존재할 때 `request.getParameter()` 를 사용하면 `request.getParameterValues()` 의 첫 번째 값을 반환한다.

