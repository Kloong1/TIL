# Spring MVC 구조

## 직접 만든 MVC 프레임워크 VS Spring MVC
지금까지 만든 MVC 프레임워크와 Spring MVC를 비교해보자.

##### 지금까지 만든 MVC 프레임워크 구조
![](스크린샷%202022-05-27%20오후%2012.57.45.png)

##### Spring MVC 구조
![](스크린샷%202022-05-27%20오후%2012.58.04.png)

핵심 구조가 완전히 똑같다!

#### 직접 만든 MVC 프레임워크 -> Sping MVC
- FrontControllerServlet -> DispatcherServlet
- handlerMappingMap -> HandlerMapping
- MyHandlerAdapter -> HandlerAdapter
- ModelView -> ModelAndView
- viewResolver -> ViewResolver (interface)
- MyView -> View (interface)


## DispatcherServlet 구조 살펴보기
- `org.springframework.web.servlet.DispatcherServlet`
- Spring MVC 역시 Front Controller 패턴으로 구현되어있다.
- DispatcherServlet이 Spring MVC의 Front Controller 역할을 담당한다.
- DispatcherServlet은 Spring MVC의 핵심이다.

### DispatcherServlet의 상속 관계
![](Pasted%20image%2020220527130918.png)
- 조상 클래스에 `HttpServlet` 이 있는 것을 확인할 수 있다.
	- `DispatcherServlet` -> `FrameworkServlet` -> `HttpServletBean` -> `HttpServlet`
	- DispatcherServlet은 서블릿으로 동작한다.
- DispatcherServlet은 Servlet이기 때문에 누군가가 등록을 해주어야 한다.
- Spring Boot가 내장 Tomcat WAS를 띄우면서, 자바 코드로 DispatcherServlet을 함께 등록한다.
	- 등록하는 동시에 DispatcherServlet을 모든 URL 경로(`urlPatterns = "/"`)에 대해서 매핑한다.
	- 참고: 더 자세한 URL 경로가 우선순위가 높다. 따라서 다른 서블릿을 더 디테일한 URL 패턴으로 매핑해서 등록해 놓았다면, 해당 URL에 대해서 등록해둔 서블릿이 정상적으로 동작한다.

### 요청 흐름
- DispatcherServlet이 호출되면 `HttpServlet` 이 제공하는 `service()`가 호출된다.
	- 다시 한번 강조하지만, DispatcherServlet 역시 Servlet이다.
- 스프링 MVC는 DispatcherServlet 의 부모인 `FrameworkServlet` 에서 `service()` 를 오버라이드 해두었다.
	- 즉 오버라이드 된 `FrameworkServlet` 의 `service()` 가 호출되게 된다.
- `FrameworkServlet` 의 `service()` 를 시작으로 여러 메서드가 호출되면서, **최종적으로DispacherServlet 의 `doDispatch()` 가 호출된다.**

### `doDispatch()` 코드 분석
DispacherServlet 의 핵심인 `doDispatch()` 코드를 분석해보자. 최대한 간단히 설명하기 위해 예외처리, 인터셉터 기능은 제외했다. 실제 코드와는 다른 점이 많으므로 직접 코드를 뜯어보는 게 정확하다.

##### DispatcherServlet.java 일부
```Java
protected void doDispatch(HttpServletRequest request, HttpServletResponse
response) throws Exception {

	HttpServletRequest processedRequest = request;
	HandlerExecutionChain mappedHandler = null;
	ModelAndView mv = null;
	
	// 1. 핸들러 조회
	mappedHandler = getHandler(processedRequest);
	if (mappedHandler == null) {
		noHandlerFound(processedRequest, response);
		return;
	}
	
	// 2. 핸들러 어댑터 조회 - 핸들러를 처리할 수 있는 어댑터
	// List를 순회하면서 supports() 메소드로 처리 가능 여부 확인
	HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
	
	// 3. 핸들러 어댑터 실행 -> 4. 핸들러 어댑터를 통해 핸들러 실행 -> 5. ModelAndView 반환
	mv = ha.handle(processedRequest, response,
	mappedHandler.getHandler());


	processDispatchResult(processedRequest, response, mappedHandler, mv,
	dispatchException);
}


private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, HandlerExecutionChain mappedHandler, ModelAndView mv, Exception exception) throws Exception {
	
	// 뷰 렌더링 호출
	render(mv, request, response);
}


protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
	
	View view;
	String viewName = mv.getViewName();
	
	// 6. 뷰 리졸버를 통해서 뷰 찾기, 7. View 반환
	view = resolveViewName(viewName, mv.getModelInternal(), locale, request);
	
	// 8. 뷰 렌더링
	view.render(mv.getModelInternal(), request, response);
}
```
- 중간 중간 코드를 많이 생략했지만, `doDispatch()` 의 핵심 흐름은 위와 같다.
- 직접 만든 MVC 프레임워크의  `FrontControllerServiceVer5` 와 비슷한 흐름으로 동작하는 것을 알 수 있다.

![](스크린샷%202022-05-27%20오후%2012.58.04.png)

#### 동작 순서
1. **핸들러 조회**: 핸들러 매핑을 통해 요청 URL에 매핑된 핸들러(컨트롤러)를 조회한다. URL 이외에도 HTTP 요청 메시지의 다양한 헤더를 활용해서 핸들러를 조회한다.
2. **핸들러 어댑터 조회**: 핸들러를 실행할 수 있는 핸들러 어댑터를 조회한다. `supports()` 메소드로 조회한다.
3. **핸들러 어댑터 실행**: 핸들러 어댑터를 실행한다.
4. **핸들러 실행**: 핸들러 어댑터가 실제 핸들러를 실행한다.
5. **ModelAndView** 반환: 핸들러 어댑터는 핸들러가 반환하는 정보를 ModelAndView로 변환해서
반환한다.
6. **viewResolver 호출**: 뷰 리졸버를 찾고 실행한다.
	- JSP의 경우: `InternalResourceViewResolver` 가 자동 등록되고, 사용된다.
7. **View 반환**: 뷰 리졸버는 뷰의 논리 이름을 물리 이름으로 바꾸고, 렌더링 역할을 담당하는 뷰 객체를반환한다.
	- JSP의 경우 `InternalResourceView(JstlView)` 를 반환하는데, 내부에 `forward()` 로직이 있다.
8. **뷰 렌더링**: 뷰를 통해서 뷰를 렌더링 한다. 


## Interface를 통한 확장성
- 스프링 MVC의 큰 강점은 DispatcherServlet 코드의 변경 없이, 원하는 기능을 변경하거나 확장할 수 있다는 점이다. 지금까지 설명한 대부분을 확장 가능할 수 있게 인터페이스로 제공한다.
- 이 인터페이스들만 구현해서 DispatcherServlet 에 등록하면 자신만의 컨트롤러를 만들 수 있다.
- 물론 실제로 확장하려고 하면 꽤나 복잡하다.

#### 주요 인터페이스 목록
- 핸들러 매핑: `org.springframework.web.servlet.HandlerMapping`
	- 직접 만들었을 때는 `Map<String, Object>` 타입으로 간단히 구현했지만, Spring에서는 URL 외에도 HTTP 헤더 등 다양한 정보로 핸들러 매핑을 하기 때문에 인터페이스로 제공한다.
- 핸들러 어댑터: `org.springframework.web.servlet.HandlerAdapter`
- 뷰 리졸버: `org.springframework.web.servlet.ViewResolver`
	- 직접 만들었을 때는 메소드로 간단히 구현했지만, Spring 에서는 다양한 View(JSP, Thymeleaf 등)를 지원하기 때문에 인터페이스로 제공한다.
- 뷰: `org.springframework.web.servlet.View`
	- 다양한 View를 지원하기 때문에 역시 인터페이스로 제공한다.


## 정리
스프링 MVC는 코드 분량도 매우 많고, 복잡해서 내부 구조를 다 파악하는 것은 쉽지 않다. 사실 해당 기능을 직접 확장하거나 나만의 컨트롤러를 만드는 일은 없으므로 걱정하지 않아도 된다. 왜냐하면 스프링 MVC는 전세계 수 많은 개발자들의 요구사항에 맞추어 기능을 계속 확장해왔고, 그래서 여러분이 웹
애플리케이션을 만들 때 필요로 하는 대부분의 기능이 이미 다 구현되어 있다. "이런 저런 기능이 필요할 것 같아!" 하면 직접 구현할 생각을 하기 전에 먼저 잘 찾아보자. 아마 그 기능은 이미 구현되어 있을 것이다.

그래도 이렇게 핵심 동작방식을 알아두어야 향후 문제가 발생했을 때 어떤 부분에서 문제가 발생했는지 쉽게 파악하고, 문제를 해결할 수 있다. 그리고 확장 포인트가 필요할 때, 어떤 부분을 확장해야 할지 감을 잡을 수 있다. 실제 다른 컴포넌트를 제공하거나 기능을 확장하는 부분들은 강의를 진행하면서 조금씩 설명하겠다. 지금은 전체적인 구조가 이렇게 되어 있구나 하고 이해하면 된다.