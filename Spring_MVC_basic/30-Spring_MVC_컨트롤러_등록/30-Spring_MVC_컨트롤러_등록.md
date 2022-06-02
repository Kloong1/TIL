# Spring MVC - 컨트롤러 등록

스프링이 제공하는 컨트롤러는 애노테이션 기반으로 동작하기 때문에 매우 유연하고 실용적이다.


## Annotation 기반의 Controller
### @RequestMapping
스프링은 애노테이션을 활용한 매우 유연하고 실용적인 컨트롤러를 만들었는데, 이것이 바로
`@RequestMapping` 애노테이션을 사용하는 컨트롤러이다.

과거에는 스프링 프레임워크가 MVC 부분이 약해서, 스프링을 사용하더라도 MVC 웹 기술은 스트럿츠 같은 다른 프레임워크를 사용했었다. 그런데 자바에 애노테이션이 생기고, `@RequestMapping` 기반의 애노테이션 컨트롤러가 등장하면서 MVC 부분도 스프링의 완승으로 끝이 났다.

### @RequestMapping 으로 등록된 컨트롤러의 조회와 호출
`@RequestMapping` 으로 등록한 컨트롤러는 `RequestMappingHandlerMapping` 이라는 핸들러 매핑에 의해 조회되고, `RequestMappingHandlerAdapter` 라는 핸들러 어댑터로 호출된다.

`RequestMappingHandlerMapping` 과 `RequestMappingHandlerAdapter` 는 앞서 봤듯이 각각 우선순위가 가장 높은 핸들러 매핑과 핸들러 어댑터이다.

실무에서는 주로 애노테이션 기반의 `@RequestMapping` 으로 컨트롤러를 등록하므로, 애노테이션 기반의 컨트롤러를 지원하는 `RequestMappingHandlerMapping` 과 `RequestMappingHandlerAdapter` 가 사용된다.

이제 우리가 직접 만든 프레임워크에서 사용했던 컨트롤러를 `@RequestMapping` 기반의 Spring MVC 컨트롤러로 변경해보자.


## @RequestMapping 으로 컨트롤러 등록

### 회원 정보 입력 Controller
##### SpringMemberFormController.java
```Java
package com.kloong.servlet.web.springmvc.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SpringMemberFormControllerVer1 {

    @RequestMapping("/springmvc/v1/members/new-form")
    public ModelAndView process() {
        return new ModelAndView("new-form");
    }
}
```
- `@Controller`
	- 이 애노테이션이 붙은 클래스는 스프링이 자동으로 스프링 빈으로 등록한다. `@Controller` 애노테이션 내부에 `@Component` 애노테이션이 있어서 컴포넌트 스캔의 대상이 된다.
	- 스프링 MVC에서 애노테이션 기반 컨트롤러로 인식한다 (아래에서 상세 설명)
- `@RequestMapping`
	- HTTP 요청 정보를 매핑하는 애노테이션. 해당 URL이 호출되면 이 애노테이션이 있는 메소드가 호출된다. 애노테이션을 기반으로 동작하기 때문에 메소드 이름은 임의로 지정해도 된다.
- `ModelAndView` 객체에 모델과 뷰 정보를 담아서 반환한다.

#### @Controller 와 RequestMappingHandlerMapping
`RequestMappingHandlerMapping` 은 핸들러 조회 시 스프링 빈 중에서 `@RequestMapping` 또는 `@Controller` 가 **클래스 레벨**에 붙어 있는 경우에 매핑 정보로 인식한다 (메소드 레벨에만 있으면 인식 못한다). 따라서 다음 코드도 동일하게 동작한다.

```Java
@Component //컴포넌트 스캔으로 스프링 빈 등록하기 위함. @Controller 내부에 들어있다.
@RequestMapping
public class SpringMemberFormControllerVer1 {

    @RequestMapping("/springmvc/v1/members/new-form")
    public ModelAndView process() {
        return new ModelAndView("new-form");
    }
}
```

실제로 `RequestMappingHandlerMapping` 의 코드를 뜯어보면 다음과 같이 `@Controller` 애노테이션 혹은 `@RequestMapping` 애노테이션이 클래스 레벨에 붙어있는지 확인하는 메서드가 존재한다.

##### RequestMappingHandlerMapping의 isHandler()
```Java
@Override
protected boolean isHandler(Class<?> beanType) {
	return (AnnotatedElementUtils.hasAnnotation(beanType, Controller.class) ||
		AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class));
}
```

### 회원 정보 저장 Controller
##### SpringMemberSaveControllerVer1.java
```Java
package com.kloong.servlet.web.springmvc.v1;

import ... //생략

@Controller
public class SpringMemberSaveControllerVer1 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @RequestMapping("/springmvc/v1/members/save")
    public ModelAndView process(HttpServletRequest request,
    HttpServletResponse response) {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        ModelAndView mv = new ModelAndView("save-result");
        mv.addObject("member", member);
        return mv;
    }
}
```
- `mv.addObject("member", member)`
	- 스프링이 제공하는 `ModelAndView` 를 통해 Model 데이터를 추가할 때는 `addObject()` 를 사용하면 된다.
	- 기존에는 `mv.getModel().put("member", member)` 이렇게 했지만 `addObject()` 를 쓰는게 훨씬 간편하다.

#### 참고: @RequestMapping 된 메소드의 매개변수
`SpringMemberSaveControllerVer1` 의  `process()` 메소드는 `HttpServletRequest` 와 `HttpServletResponse` 를 매개변수로 받는다. 하지만 `SpringMemberFormControllerVer1` 의 `process()` 는 매개변수로 아무것도 받지 않는다.

둘다 동일한 방식으로 컨트롤러 등록을 하고 호출이 되었는데 어떻게 매개변수가 다를 수 있을까?

`DispatcherServlet` 코드를 뜯어보면, 핸들러 어댑터가 핸들러를 호출하는 코드를 확인할 수 있다.
```Java
mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
```

여기서 우리가 등록한 핸들러는 `@Controller` 로 등록된 핸들러이므로 `RequestMappingHandlerMapping` 에 의해 조회되고, `RequestMappingHandlerAdapter` 에 의해 호출된다. 따라서 위 코드에서의 `ha` 는 `RequestMappingHandlerAdapter` 이다. 디버그 모드로 실행시켜보면 확인할 수 있다.

아무튼 위 코드를 보면 이전에 조회한 핸들러와 함께 `HttpServletRequest` 와 `HttpServletResponse` 객체를 매개변수로 넘기는 것을 확인할 수 있다.

실제로 `process()` 메소드가 실행되는 코드는 확인할 수 없었지만 (코드가 너무 복잡하다), 어쨌든 필요하다면 핸들러 어댑터를 통해 `HttpServletRequest` 와 `HttpServletResponse` 객체를 매개변수로 받을 수 있고, 필요 없다면 안 받을 수도 있다.

`SpringMemberSaveControllerVer1` 에서 `HttpServletResponse` 객체는 쓰이지 않길래 해당 매개변수를 없애서 다음과 같이 만든 다음 실행시켜봤다.

```Java
    @RequestMapping("/springmvc/v1/members/save")
    public ModelAndView process(HttpServletRequest request) {
    //생략
    }
```

**그랬더니 정상 동작한다!** `RequestMappingHandlerAdapter` 에서 핸들러를 호출할 때, 필요한 매개변수를 확인해서 잘 호출하는 그런 코드가 있는 것으로 추정된다.

>2022-05-02 추가
>매개변수를 이렇게 자유롭게 사용할 수 있는 이유는 Spring MVC의 컨트롤러가 애노테이션 기반이기 때문이다. `@Controller` 에서 사용 가능한 파라미터 목록은 Spring 도큐먼트에서 확인할 수 있다.
>https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-annarguments

### 회원 목록 Controller
##### SpringMemberListControllerVer1.java
```Java
package com.kloong.servlet.web.springmvc.v1;

import ... //생략

@Controller
public class SpringMemberListControllerVer1 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @RequestMapping("/springmvc/v1/members")
    public ModelAndView process() {
        List<Member> members = memberRepository.findAll();
        ModelAndView mv = new ModelAndView("members");
        mv.addObject("members", members);
        return mv;
    }
}
```


## 마치며
`@RequestMapping`  애노테이션이 클래스 단위가 아니라 메서드 단위에 적용된 것을 확인할 수 있다. 따라서 컨트롤러 클래스를 하나로 통합하는 것이 가능하다. 다음 시간에 계속...