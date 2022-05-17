# HttpServletRequest

## 기능
### HTTP 요청 메시지 파싱
HTTP 요청 메시지를 개발자가 직접 파싱해서 사용해도 되지만, 매우 불편할 것이다. 서블릿은 개발자가 HTTP 요청 메시지를 편리하게 사용할 수 있도록 개발자 대신에 HTTP 요청 메시지를 파싱한다. 그리고 그 결과를 `HttpServletRequest `객체에 담아서 제공한다.

HttpServletRequest를 사용하면 다음과 같은 HTTP 요청 메시지의 정보들을 편리하게 조회할 수 있다.

- Start line
	- HTTP 메소드
	- URL
	- 쿼리 스트링
	- 스키마, HTTP 버전
- 헤더
- 바디
	- form 파라미터 형식 조회
	- message body 데이터 직접 조회

### HttpServletRequest 객체의 부가 기능
#### 임시 저장소 기능
- 해당 HTTP 요청이 시작부터 끝날 때 까지 유지되는 임시 저장소 기능
	- 저장: `request.setAttribute(name, value)`
	- 조회: `request.getAttribute(name)`

#### 세션 관리 기능
- `request.getSession(create: true)`

>**중요**
>HttpServletRequest, HttpServletResponse를 사용할 때 가장 중요한 점은 이 객체들이 HTTP 요청 메시지, HTTP 응답 메시지를 편리하게 사용하도록 도와주는 객체라는 점이다. 따라서 이 기능에 대해서 깊이있는 이해를 하려면 **HTTP 스펙이 제공하는 요청, 응답 메시지 자체를 이해해야 한다.**


## 기본 사용법
`HttpServletRqeust` 클래스가 제공하는 다양한 메소드를 통해 HTTP 요청 메시지의 여러 정보들을 쉽게 얻을 수 있다.

##### RequestHeaderServlet.java
```Java
package com.kloong.servlet.basic.request;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "requestHeaderServlet", urlPatterns = "/request-header")
public class RequestHeaderServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printStartLine(request);
        printHeaders(request);
        printHeaderUtils(request);
        printEtc(request);
    }

    //start-line 정보
    private void printStartLine(HttpServletRequest request) {
        System.out.println("--- REQUEST-LINE - start ---");
        System.out.println("request.getMethod() = " + request.getMethod()); //GET
        System.out.println("request.getProtocol() = " + request.getProtocol()); //HTTP/1.1
        System.out.println("request.getScheme() = " + request.getScheme()); //http
        System.out.println("request.getRequestURL() = " + request.getRequestURL()); //http://localhost:8080/request-header
        System.out.println("request.getRequestURI() = " + request.getRequestURI()); //request-header
        System.out.println("request.getQueryString() = " + request.getQueryString()); //username=kloong
        System.out.println("request.isSecure() = " + request.isSecure()); //https 사용 유무
        System.out.println("--- REQUEST-LINE - end ---");
        System.out.println();
    }

    //모든 헤더 출력
    private void printHeaders(HttpServletRequest request) {
        System.out.println("--- Headers - start ---");

        /* Java 구버전에서 헤더 확인하는 방법
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }
        */

        request.getHeaderNames().asIterator()
                .forEachRemaining(headerName -> System.out.println(headerName + ": " + request.getHeader(headerName)));
        System.out.println("--- Headers - end ---");
        System.out.println();
    }

    //특정 헤더를 편리하게 출력
    private void printHeaderUtils(HttpServletRequest request) {
        System.out.println("--- Header 편의 조회 start ---");

        System.out.println("[Host 편의 조회]");
        System.out.println("request.getServerName() = " + request.getServerName()); //Host 헤더
        System.out.println("request.getServerPort() = " + request.getServerPort()); //Host 헤더
        System.out.println();

        System.out.println("[Accept-Language 편의 조회]");
        request.getLocales().asIterator()
                .forEachRemaining(locale -> System.out.println("locale = " + locale));
        System.out.println("request.getLocale() = " + request.getLocale());
        System.out.println();

        System.out.println("[cookie 편의 조회]");
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                System.out.println(cookie.getName() + ": " + cookie.getValue());
            }
        }
        System.out.println();

        //웹 브라우저에서 단순히 GET 요청을 하면 Content 관련 헤더 정보가 없다.
        //Postman으로 POST 요청을 보내서 다음 헤더들이 잘 출력되는지 확인해 볼 수 있다.
        System.out.println("[Content 편의 조회]");
        System.out.println("request.getContentType() = " + request.getContentType());
        System.out.println("request.getContentLength() = " + request.getContentLength());
        System.out.println("request.getCharacterEncoding() = " + request.getCharacterEncoding());

        System.out.println("--- Header 편의 조회 end ---");
        System.out.println();
    }

    //기타 정보. HTTP 메시지에 대한 정보는 아니다.
    private void printEtc(HttpServletRequest request) {
        System.out.println("--- 기타 조회 start ---");

        System.out.println("[Remote 정보]");
        System.out.println("request.getRemoteHost() = " + request.getRemoteHost()); //
        System.out.println("request.getRemoteAddr() = " + request.getRemoteAddr()); //
        System.out.println("request.getRemotePort() = " + request.getRemotePort()); //
        System.out.println();

        System.out.println("[Local 정보]");
        System.out.println("request.getLocalName() = " + request.getLocalName()); //
        System.out.println("request.getLocalAddr() = " + request.getLocalAddr()); //
        System.out.println("request.getLocalPort() = " + request.getLocalPort()); //

        System.out.println("--- 기타 조회 end ---");
        System.out.println();
    }
}

```