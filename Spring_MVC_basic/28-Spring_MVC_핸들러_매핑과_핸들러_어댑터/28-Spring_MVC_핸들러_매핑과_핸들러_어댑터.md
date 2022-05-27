# Spring MVC - 핸들러 매핑과 핸들러 어댑터

Spring MVC에서 어떤 핸들러 매핑과 핸들러 어댑터가, 어떻게 사용되는지 알아보자.

지금은 전혀 사용하지 않지만, 과거에 주로 사용했던 스프링이 제공하는 간단한 컨트롤러로 Spring MVC의 핸들러 매핑과 어댑터를 이해해보자.

## Controller 인터페이스
현재는 Annotation 기반의 `@Controller` 를 사용하지만, 과거에는 Controller 인터페이스를 제공했다. 참고로 `@Controller`와 `Controller` 인터페이스는 전혀 다르다.

##### org.springframework.web.servlet.mvc.Controller.java
```Java
public interface Controller {
	ModelAndView handleRequest(HttpServletRequest request,
	HttpServletResponse response) throws Exception;
}
```

이 인터페이스를 구현하는 간단한 컨트롤러를 만들어보자.

##### OldController.java
```Java
package com.kloong.servlet.web.springmvc.old;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//Spring bean의 이름을 url pattern으로 맞추면, 해당 url 요청에 대해 호출이 된다.
@Component("/springmvc/old-controller")
public class OldController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("OldController.handleRequest");
        return null;
    }
}
```
- `@Component("/springmvc/old-controller")`
	- 이 컨트롤러는 `/springmvc/old-controller` 라는 이름의 스프링 빈으로 등록되었다.
	- **빈 이름으로 URL이 매핑된다!**
	- `http://localhost:8080/springmvc/old-controller` 로 요청을 보내면 이 컨트롤러가 호출된다.

**OldController는 어떻게 호출이 된 것일까?**

### OldController가 호출되는 이유
- **HandlerMapping (핸들러 매핑)**
	- 핸들러 매핑에서 `OldController` 를 찾을 수 있어야 한다.
	- 즉 **스프링 빈의 이름으로 핸들러를 찾을 수 있는 핸들러 매핑**이 필요하다.
- **HandlerAdapter (핸들러 어댑터)**
	- 핸들러 매핑을 통해서 찾은 `OldController` 를 실행할 수 있는 핸들러 어댑터가 필요하다.
	- 즉 `Controller` 인터페이스의 구현체를 실행할 수 있는 핸들러 어댑터를 찾고 실행해야 한다.

그런데 스프링은 이미 필요한 핸들러 매핑과 핸들러 어댑터를 대부분 구현해두었다. 개발자가 직접 핸들러 매핑과 핸들러 어댑터를 만드는 일은 거의 없다.

#### Spring Boot가 자동 등록하는 핸들러 매핑과 핸들러 어댑터
실제로 자동 등록하는 핸들러 매핑과 핸들러 어댑터가 더 많지만 일부 생략했다. 숫자는 우선순위를 의미한다. 낮을 수록 우선순위가 높다. 핸들러 매핑, 핸들러 어댑터 모두 우선순위대로 찾고, 만약 없으면 다음 순서로 넘어간다.

- **HandlerMapping**
	- **0 = RequestMappingHandlerMapping** : 애노테이션 기반의 컨트롤러인 `@RequestMapping` 에서 사용된다. 최근에는 애노테이션 기반으로 개발하므로 대부분 이 핸들러 매핑이 사용된다고 보면 된다. `OldController` 에는 `@RequestMapping`이 없으므로 조회되지 않는다.
	- **1 = BeanNameUrlHandlerMapping** : 스프링 빈의 이름으로 핸들러를 찾는다. `OldController`는 요청 URL과 빈 이름이 동일하므로 이름에 의해 조회가 된다.
- **HandlerAdapter**
	- **0 = RequestMappingHandlerAdapter** : 애노테이션 기반의 컨트롤러인 `@RequestMapping`에서 사용.
	- **1 = HttpRequestHandlerAdapter** : `HttpRequestHandler` 인터페이스에 대한 핸들러 어댑터
	- **2 = SimpleControllerHandlerAdapter** : `Controller` 인터페이스(애노테이션 아님. 과거에 사용됨) 처리.

##### DispatcherServlet.java 일부
```Java
//HandlerMapping의 List - List이므로 순서가 있다.
private List<HandlerMapping> handlerMappings;

@Nullable
protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
	if (this.handlerMappings != null) {
		for (HandlerMapping mapping : this.handlerMappings) {
			HandlerExecutionChain handler = mapping.getHandler(request);
			if (handler != null) {
				return handler;
			}
		}
	}
	return null;
}
```
- `List<HandlerMapping> handlerMappings` 을 순서대로 조회한다.
- 각 `HandlerMapping` 의 구현체에서 request에 대한 handler를 조회한다.
- `OldController`는 `BeanNameUrlHandlerMapping` 에서 조회될 것이다.

##### SimpleControllerHandlerAdapter.java
```Java
package org.springframework.web.servlet.mvc;

import ... //생략

public class SimpleControllerHandlerAdapter implements HandlerAdapter {

	@Override
	public boolean supports(Object handler) {
		return (handler instanceof Controller);
	}

	@Override
	@Nullable
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		return ((Controller) handler).handleRequest(request, response);
	}
	//생략
}
```
- 이 핸들러 어댑터는 Spring Boot에 의해 `DispatcherServlet` 의 `List<HandlerAdapter> handlerAdapters` 에 자동으로 등록되어 있을 것이다.
- 그리고 `getHandlerAdapter()` 메소드에 의해 조회 될 것이다.
- `supports()` 를 보면 `Controller`의 구현체에 대해  true를 반환하는 것을 확인할 수 있다.

#### OldController가 호출되는 과정
##### 1. 핸들러 매핑으로 핸들러 조회
- `List<HandlerMapping> handlerMappings` 을 순서대로 조회하면서, `HandlerMapping.getHandler()` 를 통해 핸들러를 찾는다.
- 이 경우 빈 이름으로 핸들러를 찾아야 하기 때문에, 빈 이름으로 핸들러를 찾아주는 `BeanNameUrlHandlerMapping` 가 핸들러 조회에 성공하고 `OldController` 를 반환한다.

##### 2. 핸들러 어댑터 조회
- `List<HandlerAdapter> handlerAdapters` 를 순회하며 `HandlerAdapter` 의 `supports()` 를 순서대로 호출한다.
- `SimpleControllerHandlerAdapter` 가 `Controller` 인터페이스를 지원하므로 대상이 된다.

##### 3. 핸들러 어댑터 실행
- `DispatcherServlet` 이 조회한 `SimpleControllerHandlerAdapter` 를 실행하면서 핸들러인 `OldController` 의 정보도 함께 넘겨준다.
- `SimpleControllerHandlerAdapter` 는 `OldController` 를 내부에서 실행하고, 그 결과를 반환한다.

##### 정리 - `OldController` 핸들러 매핑 & 핸들러 어댑터
- `OldController` 를 실행하면서 사용된 객체는 다음과 같다.
	- `HandlerMapping` = `BeanNameUrlHandlerMapping`
	- `HandlerAdapter` = `SimpleControllerHandlerAdapter`


Controller 인터페이스를 구현한 핸들러가 조회되고, 그에 맞는 핸들러 어댑터가 조회되고 실행되는 과정을 알아봤다.

한 가지 타입의 핸들러의 경우만 보면 이해가 잘 안될 수도 있으니 `HttpRequestHandler` 인터페이스를 구현한 핸들러가 어떻게 조회되고 실행되는지 알아보자.


## HttpRequestHandler 인터페이스
`HttpRequestHandler` 는 **Servlet과 가장 유사한 형태의 핸들러이다.**

##### HttpRequestHandler.java
```Java
package org.springframework.web;

import ... //생략

public interface HttpRequestHandler {
	void handleRequest(HttpServletRequest request,
	HttpServletResponse response) throws ServletException, IOException;
}
```

간단하게 구현해보자.

##### MyRequestHandler.java
```Java
package com.kloong.servlet.web.springmvc.old;

import ... //생략

@Component("/springmvc/request-handler")
public class MyHttpRequestHandler implements HttpRequestHandler {
    @Override
    public void handleRequest(HttpServletRequest request
    HttpServletResponse response) throws ServletException, IOException {
        System.out.println("MyHttpRequestHandler.handleRequest");
    }
}
```
- 로직은 따로 없고 그냥 호출되면 콘솔에 출력만 하게 간단히 구현했다.
- **빈 이름이 `OldController`와 마찬가지로 요청 URL인 것을 확인할 수 있다.**

### MyHttpRequestHandler가 호출되는 과정

##### 1. 핸들러 매핑으로 핸들러 조회
- `List<HandlerMapping> handlerMappings` 을 순서대로 조회하면서, `HandlerMapping.getHandler()` 를 통해 핸들러를 찾는다.
- `MyRequestHandler` 도 `OldController` 처럼 `@RequestMapping` 애노테이션을 기반으로 하는 컨트롤러가 아니기 때문에, 빈 이름으로 핸들러를 찾아주는 `BeanNameUrlHandlerMapping` 가 핸들러 조회에 성공하고 `MyHttpRequestHandler` 를 반환한다.

##### 2. 핸들러 어댑터 조회
- `List<HandlerAdapter> handlerAdapters` 를 순회하며 `HandlerAdapter` 의 `supports()` 를 순서대로 호출한다.
- `HttpRequestHandlerAdapter` 가 `HttpRequestHandler` 인터페이스를 지원하므로 대상이 된다.

##### 3. 핸들러 어댑터 실행
- `DispatcherServlet`이 조회한 `HttpRequestHandlerAdapter` 를 실행하면서 핸들러인 `MyRequestHandler` 의 정보도 함께 넘겨준다.
- `HttpRequestHandlerAdapter` 는 `MyRequestHandler` 를 내부에서 실행하고, 그 결과를 반환한다.

##### 정리 - MyHttpRequestHandler 핸들러 매핑, 핸들러 어댑터
`MyHttpRequestHandler` 를 실행하면서 사용된 객체는 다음과 같다.
- `HandlerMapping` = `BeanNameUrlHandlerMapping`
- `HandlerAdapter` = `HttpRequestHandlerAdapter`


## @RequestMapping
조금 뒤에서 설명하겠지만, 가장 우선순위가 높은 핸들러 매핑과 핸들러 어댑터는 `@RequestMapping` 의 이름을 딴 `RequestMappingHandlerMapping`, `RequestMappingHandlerAdapter` 이다. 이 핸들러 매핑과 핸들러 어댑터가 현재 스프링에서 주로 사용하는 애노테이션 기반의 컨트롤러를 지원하는 매핑과 어댑터이다. 실무에서는 99.9% 이 방식의 컨트롤러를 사용한다. 따라서 조회 우선순위도 가장 높은 것이다.