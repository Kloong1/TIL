# MVC 패턴 - 한계

## 중간 정리
MVC 패턴을 적용한 덕분에 컨트롤러의 역할과 뷰를 렌더링 하는 역할을 명확하게 구분할 수 있었다. 특히 뷰는 화면을 그리는 역할에 충실한 덕분에, 코드가 깔끔하고 직관적이다. 단순하게 모델에서 필요한 데이터를 꺼내고, 화면을 만들면 된다

**그런데 컨트롤러의 경우, 중복이 많고 필요하지 않는 코드들도 많이 보인다.**


## MVC 컨트롤러의 단점
### Forward 중복
```Java
RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
dispatcher.forward(request, response);
```

View로 이동하는 코드가 항상 중복 호출된다. 물론 이 부분을 메서드로 공통화해도 되지만, 어쨌든 해당 메서드도 항상 직접 호출해야 한다.

### ViewPath의 중복
```
String viewPath = "/WEB-INF/views/new-form.jsp";
```
- 모든 viewPath에 항상 동일한 prefix와 suffix가 붙는다. 즉 중복이 있다.
	- prefix: `/WEB-INF/views/`
	- suffix: `.jsp`
	- View가 들어있는 폴더 명을 바꾼다면 모든 viewPath를 일일히 수정해야한다.
- 만약 JSP가 아닌 Thymeleaf 같은 다른 뷰 템플릿으로 변경한다면 전체 코드를 다 변경해야 한다.

### 사용하지 않는 코드
- `HttpServletRequest`, `HttpServletResponse` 객체를 사용하지 않는 경우가 있다.
- 특히 `HttpServletResponse` 객체는 전혀 사용하지 않고 있다.
- 그리고 `HttpServletRequest`, `HttpServletResponse` 객체를 사용하는 코드는 테스트 케이스를 작성하기 어렵다.

### 공통 처리의 어려움
기능이 복잡해질수록 컨트롤러에서 공통으로 처리해야 하는 부분이 점점 더 많이 증가할 것이다. 단순히 공통 기능을 메서드로 뽑으면 될 것 같지만, 결과적으로 해당 메서드를 항상 호출해야 하고, 실수로 호출하지 않으면 문제가 될 것이다. 그리고 호출하는 것 자체도 중복이다.


## 정리
결국 이 단점들을 해결하기 위해서는 컨트롤러를 호출하기 전에 공통 기능을 처리해 줘야 한다. 소위 수문장 역할을 하는 기능이 필요하다.

**Front Controller 패턴을 도입하면 이 문제를 해결할 수 있다.** 입구를 하나로 만들어서 수문장이 공통 기능을 처리하게끔 하면 된다.

**스프링 MVC의 핵심도 바로 이 Front Controller에 있다.**