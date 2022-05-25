# Front Controller 패턴 소개

## Front Controller 도입 전
![](스크린샷%202022-05-25%20오후%201.56.10.png)
- 입구가 여러 개인 상황.
- 공통 로직을 모든 입구에 전부 구현해둬야 한다.


## Front Controller 도입 후
![](스크린샷%202022-05-25%20오후%201.57.34.png)
- 입구가 하나이다.
- Front Controller에 필요한 공통 로직을 둔다.
- 나머지 Controller는 Front Controller에 의해 호출이 된다.


## Front Controller 패턴 특징
- 프론트 컨트롤러 서블릿 하나가 클라이언트의 요청을 받는다.
- 프론트 컨트롤러가 공통 처리를 한 뒤, 요청에 맞는 컨트롤러를 찾아서 호출해준다.
- 프론트 컨트롤러가 유일한 입구가 되기 때문에 공통 처리가 가능해진다.
- 프론트 컨트롤러를 제외한 나머지 컨트롤러는 서블릿을 사용하지 않아도 된다.
	- 서블릿은 url mapping을 해서 HTTP 요청을 받아 요청을 처리해주는 역할을 하는 것인데, HTTP 요청을 받는 작업을 프론트 컨트롤러가 다 해주기 때문.


## Spring Web MVC와 Front Controller
스프링 웹 MVC의 핵심은 Front Controller이다. **스프링 웹 MVC의 핵심인 DispatcherServlet이 Front Controller 패턴으로 구현되어 있다.**

