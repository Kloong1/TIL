# HTTP 응답 - HTTP API, 메시지 바디에 직접 입력

HTTP API를 제공하는 경우에는 HTML이 아니라 데이터 자체를 전달해야 하므로, HTTP 메시지 바디에 JSON 등의 포맷으로 데이터를 실어 보낸다.

HTTP 요청에서 응답까지 대부분 다룬 내용이므로 이번시간에는 정리를 해보자.

>**참고**
>HTML이나 뷰 템플릿을 사용해도 HTTP 응답 메시지 바디에 HTML 데이터가 담겨서 전달된다. 여기서 설명하는 내용은 정적 리소스나 뷰 템플릿을 거치지 않고, 직접 HTTP 응답 메시지에 데이터를 담아서 전달하는 경우를 말한다.


## 단순 텍스트 메시지 바디
##### ResponseBodyController.java 일부
```Java
package com.kloong.springmvc.basic.response;

import ... //생략

@Slf4j
@Controller
public class ResponseBodyController {

    @GetMapping("/response-body-string-v1")
    public void responseBodyVer1(HttpServletResponse response) throws IOException {
        response.getWriter().write("OK");
    }

    @GetMapping("/response-body-string-v2")
    public HttpEntity<String> responseBodyVer2() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("/response-body-string-v3")
    public String responseBodyVer3() {
        return "OK";
    }
}
```

##### Servlet 기술 - responseBodyVer1
- Servlet 기술인 `HttpServletResponse` 객체를 통해서 HTTP 메시지 바디에 직접 응답 메시지를 입력한다.
- - `response.getWriter().write("OK")`

##### HttpEntity/ResponseEntity - responseBodyVer2
- `ResponseEntity` 는 `HttpEntity` 를 상속 받은 클래스이다.
- `HttpEntity는` HTTP 메시지의 헤더, 바디 정보를 가지고 있다.
- `ResponseEntity` 는 여기에 더해서 HTTP 응답 코드를 설정할 수 있다.

##### @ResponseBody - responseBodyVer3
- `@ResponseBody` 를 사용하면 반환값으로 view를 찾지 않고, HTTP 메시지 컨버터를 통해서 HTTP 메시지를 직접 입력할 수 있다.
- `ResponseEntity<String>` 를 반환하는 경우에도 동일한 방식으로 동작한다.


## JSON 메시지 바디
##### ResponseBodyController.java 일부
```Java
package com.kloong.springmvc.basic.response;

import ... //생략

@Slf4j
@Controller
public class ResponseBodyController {

    @GetMapping("/response-body-json-v1")
    public ResponseEntity<HelloData> responseBodyJsonVer1() {
        HelloData helloData = new HelloData();
        helloData.setUsername("kloong");
        helloData.setAge(27);

        return new ResponseEntity<>(helloData, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/response-body-json-v2")
    public HelloData responseBodyJsonVer2() {
        HelloData helloData = new HelloData();
        helloData.setUsername("kloong");
        helloData.setAge(27);

        return helloData;
    }
}
```

##### HttpEntity/ResponseEntity - responseBodyJsonVer1
- `ResponseEntity<HelloData>` 를 반환한다. HTTP 메시지 컨버터를 통해서 객체가 JSON 형식으로 변환되어서 반환된다.

##### @ResponseBody - responseBodyJsonVer2
- `@ResponseBody` 를 사용하면 반환값으로 view를 찾지 않고, HTTP 메시지 컨버터를 통해서 HTTP 메시지를 직접 입력할 수 있다.
- 여기서는 `HelloData` 객체를 반환하므로, HTTP 메시지 컨버터가 객체를 JSON으로 변환해서 메시지 바디에 입력한다.
- `ResponseEntity` 는 HTTP 응답 코드를 설정할 수 있는데, `@ResponseBody` 를 사용하면 응답 코드를 설정하기 까다롭다.
- `@ResponseStatus` 애노테이션을 사용하면 응답 코드를 편리하게 설정할 수 있다.

>참고
>`@ResponseStatus` 로 응답 코드를 설정하는 경우, 애노테이션으로 설정한 것이기 때문에 응답 코드를 동적으로 변경할 수는 없다. 따라서 프로그램 조건에 따라서 동적으로 변경해야 한다면 `ResponseEntity` 를 사용해야 한다.


## @RestController 사용
`@Controller` 대신에 `@RestController` 애노테이션을 사용하면, 해당 컨트롤러에 모두
`@ResponseBody` 가 적용되는 효과가 있다. 따라서 뷰 템플릿을 사용하지 않고 HTTP 메시지 바디에 직접 데이터를 입력한다.

이름 그대로 REST API(HTTP API)를 만들 때 사용하는 컨트롤러이다.

참고로 `@ResponseBody` 는 클래스 레벨에 두면 전체 메서드에 적용되는데, `@RestController`
에노테이션 안에 `@ResponseBody` 가 적용되어 있다. `@RestController` 내부 코드를 보면 `@Controller`와 `@ResponseBody` 가 들어있다.