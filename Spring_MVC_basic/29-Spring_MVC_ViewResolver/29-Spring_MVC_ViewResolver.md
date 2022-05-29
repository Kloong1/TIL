# Spring MVC - ViewResolver

`null`을 반환하던 `OldController`의 `handleRequest()` 메소드가 원래 반환형에 맞게 `ModelAndView` 객체를 반환하도록 코드를 수정해보자.

##### OldController.java
```Java
package com.kloong.servlet.web.springmvc.old;  
  
import ... //생략 
  
//Spring bean의 이름을 url pattern으로 맞추면, 해당 url 요청에 대해 호출이 된다.  
@Component("/springmvc/old-controller")  
public class OldController implements Controller {  
    @Override  
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {  
        System.out.println("OldController.handleRequest");  
        return new ModelAndView("new-form");  
    }  
}
```

이제 Spring Boot로 애플리케이션을 실행시킨 뒤 `http://localhost:8080/springmvc/old-controller` 로 요청을 보내면, `handleRequest()` 는 정상적으로 호출되지만 웹 브라우저에는 `Whitelabel Error Page` 가 나온다.

즉 컨트롤러는 정상적으로 호출 되지만, `new-form` 이라는 논리 이름을 가진 뷰를 찾지 못한 것이다. 논이 이름을 통해 뷰를 찾을 수 있게 만들어보자.

설정 파일인 `application.properties` 에 뷰의 이름에 붙을 prefix와 suffix를 알려주기만 하면 된다.

##### application.properties
```
spring.mvc.view.prefix=/WEB-INF/views/  
spring.mvc.view.suffix=.jsp
```

Spring Boot로 애플리케이션을 실행시킨 뒤 `http://localhost:8080/springmvc/old-controller` 로 요청을 보내면 `/WEB-INF/views/new-form.jsp` 가 웹 브라우저에 정상적으로 나타나는 것을 확인할 수 있다.

>**참고**
>권장하지는 않지만 `application.properties` 설정 없이 다음과 같이 전체 경로를 주어도 동작하기는 한다. `return new ModelAndView("/WEB-INF/views/new-form.jsp");`

### ViewResolver - InternalResourceViewResolver
Spring Boot는 스프링을 올리면서 `InternalResourceViewResolver` 라는 ViewResolver를 빈으로 등록한다. 이 때 `application.properties`의 `spring.mvc.view.prefix` 와 `spring.mvc.view.suffix` 정보를 사용해서 `InternalResourceViewResolver` 빈을 등록한다.

##### InternalResourceViewResolver 빈 등록 예시
```Java
@Bean
ViewResolver internalResourceViewResolver() {
	return new InternalResourceViewResolver("/WEB-INF/views/", ".jsp");
}
```

실제로 `InternalResourceViewResolver` 에는 위 코드처럼 prefix와 suffix를 전달받는 생성자가 존재한다. 물론 Spring boot가 `InternalResourceViewResolver` 빈 등록을 알아서 해주기 때문에 직접 할 필요는 없다.

## ViewResolver 동작 방식

##### Spring MVC 구조
![](스크린샷%202022-05-29%20오후%205.33.19.png)
- 5번 단계에서 `ModelAndView`를 반환 받으면, `ModelAndView` 객체에 담긴 뷰의 논리 이름을 가지고 뷰 리졸버를 호출한다.

### Spring Boot가 자동 등록하는 ViewResolver
- **1 = BeanNameViewResolver**: 빈 이름으로 뷰를 찾아서 반환한다. (예: 엑셀 파일 생성 기능에 사용)
- **2 = InternalResourceViewResolver**: JSP를 처리할 수 있는 뷰를 반환한다.
- 실제로는 더 많지만 생략했다.

### InternalResourceViewResolver 동작 과정

##### 1. 핸들러 어댑터 호출
핸들러 어댑터를 통해 `ModelAndView` 객체를 반환 받는다. 객체 내부에는 `new-form` 이라는 논리 뷰 이름이 있다.

##### 2. ViewResolver 호출
`new-form` 이라는 뷰 이름으로 ViewResolver를 순서대로 호출한다. `BeanNameViewResolver` 는 `new-form` 이라는 이름의 스프링 빈으로 등록된 뷰를 찾는다. 하지만 이 프로젝트에는 그런 빈이 존재하지 않기 때문에 찾을 수 없다. 따라서 다음 우선순위를 가진 `InternalResourceViewResolver` 가 호출된다.

##### 3. InternalResourceViewResolver
`InternalResourceViewResolver` 는 이름 그대로 내부에서 자원을 찾을 수 있는 경우에 사용한다. 이 ViewResolver는 단순히 `InternalResourceView` 를 반환한다. 

##### 4.  InternalResourceView
`InternalResourceView `는 JSP처럼 `forward()` 를 호출해서 처리할 수 있는 경우에 사용하는 View이다.

##### 5. view.render()
`view.render()` 가 호출되고 `InternalResourceView` 는 `forward()` 를 사용해서 JSP를 실행한다. 내부 코드를 살펴보면 `forward()` 메소드가 호출되는 것을 확인할 수 있다.

>**참고 1**
>`InternalResourceViewResolver` 는 만약 JSTL 라이브러리가 있으면 `InternalResourceView` 를 상속받은 `JstlView` 를 반환한다. `JstlView` 는 JSTL 태그 사용시 약간의 부가 기능이 추가된다.

>**참고 2**
>다른 뷰는 실제 뷰를 렌더링하지만, JSP의 경우 `forward()` 메소드를 통해서 해당 JSP로 이동(실행)해야 렌더링이 된다. JSP를 제외한 나머지 뷰 템플릿들은 `forward()` 과정 없이 바로 렌더링 된다.

>**참고 3**
>Thymeleaf 뷰 템플릿을 사용하면 `ThymeleafViewResolver` 를 등록해야 한다. 최근에는 라이브러리만 추가하면 스프링 부트가 이런 작업도 모두 자동화해준다.