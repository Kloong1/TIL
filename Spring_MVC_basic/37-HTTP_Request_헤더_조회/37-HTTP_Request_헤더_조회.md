# HTTP Request - 기본, 헤더 조회

Spring을 활용해서 데이터 전달하는 방법을 알아보기 전에, 먼저 HTTP 요청의 헤더를 조회하는 방법을 알아보자. Servlet이 지원하는 방식보다 훨씬 편리하다.

Spring의 **애노테이션 기반의 스프링 컨트롤러** (클래스 레벨에 `@Controller` 혹은 `@RequestMapping` 이 붙어있는 클래스. `RequestMappingHandlerAdapter` 에 의해 호출된다) 는 다양한 핸들러 어댑터로부터 다양한 파라미터를 전달받을 수 있다.

**왜냐하면 인터페이스 기반이 아닌 애노테이션 기반이기 때문이다! Spring이 지원하는 타입의 파라미터라면 Spring이 알아서 전달해준다.**

컨트롤러 메소드의 반환형도 파라미터와 마찬가지로 Spring이 지원하는 타입 중에서 골라서 사용할 수 있다.

>참고 1
>`@Controller` 에서 사용 가능한 파라미터 목록은 Spring 도큐먼트에서 확인할 수 있다.
>https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-annarguments

>참고2
>@Controller 에서 사용 가능한 반환형 목록은 Spring 도큐먼트에서 확인할 수 있다. 반환형에 따라 내부에서 일어나는 동작에 대해서도 설명이 되어있다.
>https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-annreturn-types

```Java
package com.kloong.springmvc.basic.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Slf4j
@RestController
public class RequestHeaderController {

    @RequestMapping("/headers")
    public String headers(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpMethod httpMethod,
                          Locale locale,
                          @RequestHeader MultiValueMap<String, String> headerMap,
                          @RequestHeader("host") String host,
                          @CookieValue(value = "myCookie", required = false) String cookie) {

        log.info("request={}", request);
        log.info("response={}", response);
        log.info("httpMethod={}", httpMethod);
        log.info("locale={}", locale);
        log.info("headerMap={}", headerMap);
        log.info("header host={}", host);
        log.info("myCookie={}", cookie);

        return "OK";
    }
}
```
- `headers()` 의 파라미터가 매우 많다. 하지만 애노테이션 기반의 컨트롤러이기 때문에 Spring이 알아서 다 전달해줄 수 있다!
- `HttpServletRequest`, `HttpServletResponse` 는 Servlet에서 지원하는 타입의 파라미터이다.
- Locale은 자바에서 지원한다.
- 나머지 파라미터는 전부 Spring에서 지원하는 타입의 파라미터이다.
- `@RequestHeader MultiValueMap<String, String>`
	- 모든 HTTP 헤더를 `MultiValueMap` 형식으로 조회한다.
	- `MultiValueMap` 역시 Spring에서 지원한다.
	- `MultiValueMap<K, V>` 는 `Map<K, List<V>` 를 상속받는다.
	- 하나의 key에 여러 value를 받을 수 있다.
	- 따라서 HTTP 헤더, HTTP 쿼리 파라미터 같이 하나의 키에 여러 값을 받아야 하는 경우에 유용하다.
- `@RequestHeader("헤더 이름")`
	- 특정 HTTP 헤더를 조회한다.
	- 속성
		- `required` : 이 헤더가 반드시 존재해야 하는지 여부
		- `defaultValue` : 기본값
- `@CookieValue(value = "myCookie", required = false)`
	- 특정 이름을 가진 쿠키를 조회한다.
	- 속성
		- `required` : 이 쿠키가 반드시 존재해야 하는지 여부 (default: true)
		- `defaultValue` : 기본값


##### 참고: MultiValueMap
- 하나의 key에 여러 value를 저장할 수 있는 Map
- 따라서 HTTP 헤더, HTTP 쿼리 파라미터 같이 하나의 키에 여러 값을 받아야 하는 경우에 유용하다.
	- ?keyA=value1&keyA=value2
```Java
MultiValueMap<String, String> map = new LinkedMultiValueMap();
map.add("keyA", "value1");
map.add("keyA", "value2");

//[value1,value2]
List<String> values = map.get("keyA");
```


