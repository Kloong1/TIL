# Front Controller Ver. 3 - Model 추가

## 서블릿 종속성 제거
(프론트 컨트롤러가 아닌) 컨트롤러 입장에서 `HttpServletRequest`, `HttpServletResponse` 객체는 반드시 필요하지 않다. 물론 프로젝트마다, 또 컨트롤러마다 다르겠지만, 해당 객체가 가지고 있는 모든 HTTP 요청/응답 메시지 정보가 필요한 경우는 많지 않을 것이다.

따라서 우리 프로젝트에서는 HTTP 요청 메시지에 담겨있는 요청 파라미터 정보를 Map에 담아서 넘기게끔 구현하면, 컨트롤러가 서블릿 기술을 몰라도(서블릿 관련 객체에 의존하지 않아도) 동작할 수 있다.

이를 위해서 request 객체를 Model로 사용하는 대신에 별도의 Model 객체를 만들어서 반환하게끔 코드를 수정해보자. 우리가 구현하는 컨트롤러가 서블릿 기술을 전혀 사용하지 않도록 해보자.

이렇게 하면 구현 코드도 매우 단순해지고, 테스트 코드 작성이 쉽다.


## View 이름 중복 제거
컨트롤러에서 뷰의 이름(뷰의 경로)를 지정할 때 중복이 있는 것을 확인할 수 있다.
```Java
return new MyView("/WEB-INF/views/new-form.jsp");
return new MyView("/WEB-INF/views/members.jsp");
return new MyView("/WEB-INF/views/save-result.jsp");
```

이렇게 뷰를 담당하는 파일의 상위 경로(`/WEB-INF/views/`)와 확장자(`.jsp`)가 중복되어 나타난다.

따라서 컨트롤러는 뷰의 논리 이름(`new-form`, `members`, `save-result`)을 반환하고, 실제 물리 위치의 이름(전체 경로와 확장자를 포함한 이름)은 프론트 컨트롤러에서 처리하도록 단순화 하자.

이렇게 해두면 나중에 뷰의 폴더 위치가 바뀌어도 프론트 컨트롤러만 고치면 된다.


## Ver.3 구조
![](스크린샷%202022-05-26%20오후%202.58.11.png)
- Ver.2 에서는 Controller가 MyView를 반환했지만, Ver.3에서는 Model과 View가 섞여있는 ModelView를 반환한다.
- Controller가 반환하는 view의 정보에는 물리 이름이 아닌 논리 이름만 담겨있다.
- ViewResolver가 view의 논리 이름을 물리 이름으로 변환하며 MyView를 반환한다.
- 마지막으로 MyView의 `render()`를 호출하며, model을 넘겨준다.

#### ModelView
Ver.2 까지는 컨트롤러가 `HttpServletRequest`, `HttpServletResponse` 객체를 사용하면서 서블릿에 종속적으로 동작했다. `HttpServletRequest` 객체를 model로 사용해서 데이터를 저장하고 view에 전달했다.

서블릿 종속성을 제거하기 위해 Model을 직접 만들고, 추가로 View의 논리 이름을 전달하는 객체가 필요한데, 그것이 바로 ModelView이다. Controller에서는 더이상 `HttpServletRequest`, `HttpServletResponse` 객체를 사용하지 않게 하고, ModelView 객체

>**참고**
>ModelView는 이후 다른 버전에서도 사용할 예정이므로 frontcontroller 패키지에 위치시킨다.
>또 이름이 ModelView 처럼 기묘한 이유는, Spring에 ModelAndView라는 클래스가 이미 있기 때문에 헷갈림 방지를 위해서이다.


## Ver.3 구현
### ModelView 구현
##### ModelView.java
```Java
package com.kloong.servlet.web.frontcontroller;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class ModelView {
    private String viewName;
    private Map<String, Object> model = new HashMap<>();

    public ModelView(String viewName) {
        this.viewName = viewName;
    }
}
```
- **viewName**: View의 논리 이름을 저장한다.
- **model**: 실질적으로 model의 역할을 할 Map. View의 역할을 하는 JSP가 key는 String, value는 Object인 map 에서 데이터를 꺼내어 사용하므로 Map을 사용하면 된다.

### Controller 구현
##### ControllerVer3.java
```Java
package com.kloong.servlet.web.frontcontroller.ver3;

import com.kloong.servlet.web.frontcontroller.ModelView;

import java.util.Map;

public interface ControllerVer3 {
    //Servlet 기술에 종속적이지 않음.
    ModelView process(Map<String, String> paramMap);
}
```
- ControllerVer3 인터페이스는 `HttpServletRequest`, `HttpServletResponse` 객체를 전달받지 않는다. 즉 서블릿 기술에 종속적이지 않다. 따라서 구현이 단순해지고, 테스트 코드 작성이 쉽다.
- `HttpServletRequest` 객체에 담겨있는 요청 파라미터는 프론트 컨트롤러가 `Map<String, String> paramMap` 에 담아서 넘겨주면 된다.
- View의 논리 이름과 Model 데이터를 포함하는 ModelView 객체를 반환한다.

##### MemberFormControllerVer3.java
```Java
package com.kloong.servlet.web.frontcontroller.ver3.controller;

import com.kloong.servlet.web.frontcontroller.ModelView;
import com.kloong.servlet.web.frontcontroller.ver3.ControllerVer3;

import java.util.Map;

public class MemberFormControllerVer3 implements ControllerVer3 {

    @Override
    public ModelView process(Map<String, String> paramMap) {
        return new ModelView("new-form");
    }
}
```
- ModelView 객체를 생성할 때 `new-form` 과 같은 view의 논리 이름만 사용하는 것을 확인할 수 있다.
- 실제 view의 물리적 이름은 프론트 컨트롤러에서 처리하도록 맡긴다.

##### MemberSaveControllerVer3.java
```Java
package com.kloong.servlet.web.frontcontroller.ver3.controller;

import com.kloong.servlet.domain.member.Member;
import com.kloong.servlet.domain.member.MemberRepository;
import com.kloong.servlet.web.frontcontroller.ModelView;
import com.kloong.servlet.web.frontcontroller.ver3.ControllerVer3;

import java.util.Map;

public class MemberSaveControllerVer3 implements ControllerVer3 {
    private MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    public ModelView process(Map<String, String> paramMap) {
        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        ModelView mv = new ModelView("save-result");
        mv.getModel().put("member", member);
        return mv;
    }
}
```
- 프론트 컨트롤러가 `HttpServletRequest` 객체에서 요청 파라미터를 꺼낸 뒤, `Map<String, String> paramMap` 에 담아서 넘겨줬다. `paramMap` 에서 파라미터를 꺼내서 사용한다.
- Ver.2와 동일하게 비즈니스 로직을 수행한다.
- ModelView 객체에 view의 논리 이름과 Model 데이터를 저장해서 반환한다.

##### MemberListControllerVer3.java
```Java
package com.kloong.servlet.web.frontcontroller.ver3.controller;

import com.kloong.servlet.domain.member.Member;
import com.kloong.servlet.domain.member.MemberRepository;
import com.kloong.servlet.web.frontcontroller.ModelView;
import com.kloong.servlet.web.frontcontroller.ver3.ControllerVer3;

import java.util.List;
import java.util.Map;

public class MemberListControllerVer3 implements ControllerVer3 {
    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {
        List<Member> members = memberRepository.findAll();
        ModelView mv = new ModelView("members");
        mv.getModel().put("members", members);

        return mv;
    }
}
```

### Front Controller 구현
##### FrontControllerServletVer3.java
```Java
package com.kloong.servlet.web.frontcontroller.ver3;

import ... //생략

// /front-controller/v3/ 의 모든 하위 경로 URL 요청이 이 서블릿으로 전달된다.
@WebServlet(name = "frontControllerServletVer3", urlPatterns = "/front-controller/v3/*")
public class FrontControllerServletVer3 extends HttpServlet {

    private Map<String, ControllerVer3> controllerMap = new HashMap<>();

    private static final String VIEW_PATH = "/WEB-INF/views/";
    private static final String VIEW_EXTENSION = ".jsp";

    public FrontControllerServletVer3() {
        controllerMap.put("/front-controller/v3/members/new-form",
        new MemberFormControllerVer3());
        controllerMap.put("/front-controller/v3/members/save",
        new MemberSaveControllerVer3());
        controllerMap.put("/front-controller/v3/members",
        new MemberListControllerVer3());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();//서버의 IP와 port 이후의 URI를 얻을 수 있음. 즉 생성자에서 mapping한 request URI를 얻을 수 있다.

        ControllerVer3 controller = controllerMap.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Map<String, String> paramMap = createParamMap(request);
        ModelView modelView = controller.process(paramMap);

        MyView view = viewResolver(modelView.getViewName());
        view.render(modelView.getModel(), request, response);
    }

    private MyView viewResolver(String viewName) {
        return new MyView(VIEW_PATH + viewName + VIEW_EXTENSION);
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
```

#### Ver.2 와 달라진 점
- `createParamMap()` 메소드로 request 객체에 담긴 요청 파라미터를 전부 꺼내서 paramMap을 만든다. 그리고 request와 response 객체 대신 paramMap을 Controller에 넘겨준다.
- Controller는 MyView 객체가 아닌 ModelView 객체를 반환한다.
- `viewResolver()` 메소드로 view의 논리 이름을 물리 이름으로 변환한 뒤 해당 물리 이름을 사용하여 MyView 객체를 만든다. 이를 통해 중복을 제거하고 공통 처리가 가능해졌다. View가 위치한 폴더가 바뀌어도 프론트 컨트롤러에서 경로만 바꿔주면 된다.
- `view.render()` 를 호출하며 ModelView 객체의 `Map<String, Object> model` 을 같이 넘겨준다.

### MyView 수정
```Java
package com.kloong.servlet.web.frontcontroller;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class MyView {

    private String viewPath;

    public MyView(String viewPath) {
        this.viewPath = viewPath;
    }

    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }

    //view에서는 request와 response 객체를 사용한다. 즉 JSP는 서블릿에 의존한다.
    //JSP가 request.getAttribute()를 사용할 수 있게 하기 위해 model에 담긴 파라미터를 전부 request에 넘겨줘야한다.
    public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        modelToRequestArrtibute(model, request);
        render(request, response);
    }

    private void modelToRequestArrtibute(Map<String, Object> model, HttpServletRequest request) {
        model.forEach((key, value) -> request.setAttribute(key, value));
    }
}
```
- `Map<String, Object> model` 을 함께 넘겨받는 `render()` 메소드를 추가했다.
- `model` 에는 컨트롤러에서 처리하여 view에 넘겨줘야 하는 정보가 담겨있다. 즉 Ver.2와 달리 request에 model 데이터를 담아서 넘겨주지 않는다.
- 그러나 JSP는 `request.getAttribute()` 로 데이터를 조회하기 때문에, 실질적으로는 `modelToRequestAttribute()` 메소드를 사용해서 request 객체에 model 데이터를 담아야한다.
	- 결국 View의 관점에서 봤을 때는 달라진 부분이 없다. 여전히 request 객체를 사용하기 때문이다. 하지만 이는 JSP가 서블릿에 의존적이기 떄문이다.
	- 컨트롤러의 관점에서 봤을 때는 컨트롤러가 더이상 서블릿에 의존하지 않기 때문에 유의미한 변화이다.
- 마지막으로 JSP로 forward 한다.

