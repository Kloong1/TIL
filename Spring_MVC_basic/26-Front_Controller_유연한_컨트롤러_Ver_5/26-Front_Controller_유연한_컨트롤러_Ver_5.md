# Front Controller Ver. 5 - 유연한 컨트롤러

만약 한 프로젝트 안에서 어떤 모듈에 대해서는 ControllerVer3 인터페이스를 구현해서 컨트롤러를 개발하고 싶고, 다른 모듈에 대해서는 ControllerVer4 인터페이스를 구현해서 컨트롤러를 개발하고 싶다면 어떻게 해야할까?

##### ControllerVer3.java
```Java
package com.kloong.servlet.web.frontcontroller.ver3;

import ... //생략

public interface ControllerVer3 {
    ModelView process(Map<String, String> paramMap);
}
```

##### ControllerVer4.java
```Java
package com.kloong.servlet.web.frontcontroller.ver4;

import ... //생략

public interface ControllerVer4 {
    String process(Map<String, String> paramMap, Map<String, Object> model);
}
```

지금까지 개발한 프론트 컨트롤러는 하나의 컨트롤러 인터페이스만 사용할 수 있다.

##### FrontControllerVer4.java
```Java
//package, import 생략
public class FrontControllerServletVer4 extends HttpServlet {

    private Map<String, ControllerVer4> controllerMap = new HashMap<>();
    
    public FrontControllerServletVer3() {
        controllerMap.put("/front-controller/v4/members/new-form",
        new MemberFormControllerVer4());
        //이하 생략
    }
    //다른 메소드 생략
}
```

controllerMap의 타입이 `Map<String, ControllerVer4>` 으로, value의 타입이 ControllerVer4로 고정되어 있기 떄문에 controllerMap에 ControllerVer3의 구현체를 집어 넣을 수가 없다.

이럴 때 **어댑터 패턴**을 사용할 수 있다. 어댑터 패턴을 사용해서 프론트 컨트롤러가 다양한 방식의 컨트롤러를 처리할 수 있도록 변경해보자.


## Ver.5 구조 - Adapter 패턴
![](스크린샷%202022-05-26%20오후%2010.53.53.png)
- **핸들러**: 이전과는 달리 어댑터의 개념이 추가되었기 때문에, 컨트롤러를 포함해서 어떤 것이든 어댑터만 있으면 프론트 컨트롤러가 전부 처리할 수 있다. 따라서 컨트롤러보다 더 넓은 의미를 가진 핸들러라는 이름을 사용한다.
- **핸들러 어댑터**: 다양한 종류의 컨트롤러(이제는 컨트롤러를 넘어서 더 넓은 범위의 핸들러)를 호출할 수 있게 하는 역할을 한다.
- 프론트 컨트롤러는 컨트롤러(이제부터는 핸들러)를 직접 호출하는 것이 아니라, 핸들러 어댑터를 거쳐서 호출하게 된다.

## Ver.5 구현

### Handler Adapter
##### MyHandlerAdapter.java
```Java
package com.kloong.servlet.web.frontcontroller.ver5;

import com.kloong.servlet.web.frontcontroller.ModelView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MyHandlerAdapter {

    boolean supports(Object handler);

    ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException;
}
```
- `boolean supprots(Object handler)`
	- 어댑터가 `handler` 를 처리할 수 있는지 판단하는 메서드이다.
	- 처리할 수 있으면 true, 없으면 false를 반환한다.
	- 프론트 컨트롤러가 HTTP 요청을 받으면, 해당 요청에 대한 핸들러를 조회한다.
	- 핸들러를 얻었으면, 핸들러를 처리할 수 있는 핸들러 어댑터를 또 조회해야하는데, 이 때 이 메서드가 쓰인다.
- `ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler)`
	- 어댑터는 핸들러를 호출하고, 그 결과로 ModelView 객체를 반환해야 한다.
	- 만약 핸들러가 ModelView를 반환하지 않는다면, 어댑터가 ModelView를 생성해서 반환해야 한다.
	- 이전에는 프론트 컨트롤러가 컨트롤러(핸들러)를 직접 호출했지만, 이제는 어댑터를 통해서 호출해야 한다.

>**참고**
>이름이 MyHandlerAdapter인 이유는 이전의 경우처럼 Spring에 HandlerAdapter가 있기 때문이다. 헷갈림 방지용이다.

##### ControllerVer3HandlerAdapter.java
```Java
package com.kloong.servlet.web.frontcontroller.ver5.adapter;

import ... //생략

public class ControllerVer3HandlerAdapter implements MyHandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerVer3);
    }

    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        //supports로 검증한 후에 이 메소드가 호출되기 때문에 다운캐스팅해도 된다.
        ControllerVer3 controller = (ControllerVer3) handler;

        Map<String, String> paramMap = createParamMap(request);
        ModelView mv = controller.process(paramMap);
        
        return mv;
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
```
- `supports()` 로 handler가 `ControllerVer3` 의 구현체인지 확인한다. 구현체이면 이 어댑터로 처리 가능한 핸들러이므로 프론트 컨트롤러에서 이 어댑터를 사용할 것이다.
- `handle()` 에서는 먼저 handler를 ControllerVer3으로 다운캐스팅 한다. `handle()` 을 호출하기 전에 반드시  `supports()` 로 handler가 ControllerVer3의 구현체인 것을 확인하므로 문제 없다.
	- `ControllerVer3` 타입의 handler가 아닌 `Object` 타입의 handler를 받는 이유는 `MyHandlerAdapter`를 구현해야 하기 때문이다.
- 프론트 컨트롤러 대신 컨트롤러를 호출해야 하므로 Ver.3에서 프론트 컨트롤러가 하던 작업(paramMap 만들기)을 대신 한 뒤 컨트롤러를 호출한다.
- 마지막으로 ModelView 객체를 반환한다.

### Front Controller 구현
##### FrontControllerServletVer5.java
```Java
package com.kloong.servlet.web.frontcontroller.ver5;

import ... //생략

@WebServlet(name = "frontControllerServletVer5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletVer5 extends HttpServlet {

    //이전에는 Map<String, ControllerVerX> 타입의 map을 사용했지만
    //이제는 handler에 어떤 타입의 객체가 올 지 모르기 때문에 Object 타입을 value로 받아야 한다.
    private final Map<String, Object> handlerMappingMap = new HashMap<>();

    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    private static final String VIEW_PATH = "/WEB-INF/views/";
    private static final String VIEW_EXTENSION = ".jsp";

    public FrontControllerServletVer5() {
        initHandlerMappingMap();
        initHandlerAdapters();
    }

    private void initHandlerAdapters() {
        handlerAdapters.add(new ControllerVer3HandlerAdapter());
    }

    private void initHandlerMappingMap() {
        handlerMappingMap.put("/front-controller/v3/members/new-form", new MemberFormControllerVer3());
        handlerMappingMap.put("/front-controller/v3/members/save", new MemberSaveControllerVer3());
        handlerMappingMap.put("/front-controller/v3/members", new MemberListControllerVer3());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //HTTP 요청 URI에 해당하는 핸들러를 조회함.
        Object handler = getHandler(request);
        if (handler == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //조회한 핸들러를 처리할 수 있는 핸들러 어댑터를 조회함
        MyHandlerAdapter handlerAdapter = getHandlerAdapter(handler);

        ModelView mv = handlerAdapter.handle(request, response, handler);

        MyView view = viewResolver(mv.getViewName());
        view.render(mv.getModel(), request, response);
    }

    private MyHandlerAdapter getHandlerAdapter(Object handler) {
        for (MyHandlerAdapter handlerAdapter : handlerAdapters) {
            if (handlerAdapter.supports(handler)) {
                return handlerAdapter;
            }
        }
        throw new IllegalArgumentException("Handler Adapter Not Found Error - " + handler);
    }

    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
    }

    private MyView viewResolver(String viewName) {
        return new MyView(VIEW_PATH + viewName + VIEW_EXTENSION);
    }
}
```
- `Map<String, Object> handlerMappingMap`
	- `Map<String, ControllerVerX>` 타입이 아니다.
	- 어떤 핸들러든 프론트 컨트롤러에서 처리 가능해야 하므로 `Object` 타입이어야 한다.
- `List<MyHandlerAdapter> handlerAdapters`
	- 핸들러 어댑터의 List이다. 이 List에서 핸들러에 맞는 핸들러 어댑터를 조회한다.
	- `getHanlerAdapter()` 메소드에서 `supports()` 메소드로 핸들러를 처리 가능한 어댑터를 조회하는 것을 확인할 수 있다.
	- 처리 가능한 핸들러 어댑터가 없으면 예외를 던진다.
- 요청에 대한 핸들러를 조회하고, 핸들러에 대한 핸들러 어댑터를 조회하면 그 이후는 비슷하다. 핸들러 어댑터를 호출해서 핸들러를 실행시키고, ModelView를 반환받은 뒤, `render()` 한다.
	- 프론트 컨트롤러가 핸들러를 직접 실행하지 않는다.

### 그런데...
아직은 `ControllerVer3`를 구현한 핸들러와 이 핸들러들을 처리할 수 있는 `ControllerVer3HandlerAdapter`만 있기 때문에 오히려 더 복잡해지기만 한 것 같다.

지금부터 `ControllerVer4` 핸들러도 함께 처리할 수 있게 Front Controller에 코드를 추가하자. 이렇게 되면 맨 처음에 언급했던 기존의 목적(모듈에 따라 다른 컨트롤러 타입을 쓰고 싶다)을 달성할 수 있을 것이다.


## Ver.5 추가 구현
### Handler Adapter - ControllerVer4 처리하는 어댑터
##### ControllerVer4HandlerAdapter.java
```Java
package com.kloong.servlet.web.frontcontroller.ver5.adapter;

import ... /생략

public class ControllerVer4HandlerAdapter implements MyHandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerVer4);
    }

    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        ControllerVer4 controller = (ControllerVer4)handler;

        Map<String, String> paramMap = createParamMap(request);
        Map<String, Object> model = new HashMap<>();

        String viewName = controller.process(paramMap, model);

        ModelView mv = new ModelView(viewName);
        mv.setModel(model);

        return mv;
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
```
- `ControllerVer4` 는 `Map<String, String> paramMap` 에 더해 `Map<String, Object> model` 을 추가로 넘겨 받아서 비즈니스 로직을 수행하므로, `model` 을 만들어서 넘겨주는 코드가 필요하다.
- 또 `ControllerVer4` 는 viewName만 반환하므로, ModelView 객체를 만들어서 반환하는 코드가 필요하다.
	- 이 부분에서 어댑터의 강력함을 발견할 수 있다.
	- 어댑터가 Controller의 종류에 따라서 다른 방식으로 동작하고, 반환형을 동일하게 맞춰주기 때문에 프론트 컨트롤러는 다양한 형태의 컨트롤러를 처리할 수 있게 된다.

### Front Controller
##### FrontControllerServletVer5.java
```Java
package com.kloong.servlet.web.frontcontroller.ver5;

import ... //생략

@WebServlet(name = "frontControllerServletVer5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletVer5 extends HttpServlet {

    //이전에는 Map<String, ControllerVerX> 타입의 map을 사용했지만
    //이제는 handler에 어떤 타입의 객체가 올 지 모르기 때문에 Object 타입을 value로 받아야 한다.
    private final Map<String, Object> handlerMappingMap = new HashMap<>();

    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    private static final String VIEW_PATH = "/WEB-INF/views/";
    private static final String VIEW_EXTENSION = ".jsp";

    public FrontControllerServletVer5() {
        initHandlerMappingMap();
        initHandlerAdapters();
    }

    private void initHandlerAdapters() {
        handlerAdapters.add(new ControllerVer3HandlerAdapter());
        handlerAdapters.add(new ControllerVer4HandlerAdapter());
    }


    private void initHandlerMappingMap() {
        //URL 계층이 하나 추가된 것에 유의하자 (/v5/v3/)
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form",
        new MemberFormControllerVer3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save",
        new MemberSaveControllerVer3());
        handlerMappingMap.put("/front-controller/v5/v3/members",
        new MemberListControllerVer3());

        handlerMappingMap.put("/front-controller/v5/v4/members/new-form",
        new MemberFormControllerVer4());
        handlerMappingMap.put("/front-controller/v5/v4/members/save",
        new MemberSaveControllerVer4());
        handlerMappingMap.put("/front-controller/v5/v4/members",
        new MemberListControllerVer4());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //HTTP 요청 URI에 해당하는 핸들러를 조회함.
        Object handler = getHandler(request);
        if (handler == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //조회한 핸들러를 처리할 수 있는 핸들러 어댑터를 조회함
        MyHandlerAdapter handlerAdapter = getHandlerAdapter(handler);

        ModelView mv = handlerAdapter.handle(request, response, handler);

        MyView view = viewResolver(mv.getViewName());
        view.render(mv.getModel(), request, response);
    }

    private MyHandlerAdapter getHandlerAdapter(Object handler) {
        for (MyHandlerAdapter handlerAdapter : handlerAdapters) {
            if (handlerAdapter.supports(handler)) {
                return handlerAdapter;
            }
        }
        throw new IllegalArgumentException("Handler Adapter Not Found Error - " + handler);
    }

    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
    }

    private MyView viewResolver(String viewName) {
        return new MyView(VIEW_PATH + viewName + VIEW_EXTENSION);
    }
}
```
- 핵심 코드는 그대로 둔 채로, ControllerVer4를 구현한 컨트롤러만 `handlerMappingMap`에 추가하고, 해당 핸들러들을 처리하는 어댑터를 `handlerAdapters`에 추가했다.
- 프론트 컨트롤러는 MyHandlerAdapter 인터페이스에만 의존하고 있다. 핸들러도 `Object` 타입이기 때문에 어떤 객체가 들어와도 상관 없다.
- 핸들러 어댑터를 활용하여 높은 확장성을 가진 프론트 컨트롤러를 구현하는 데 성공했다.


## 정리
지금까지 Ver.1 ~ Ver.5로 점진적으로 프레임워크를 발전시켜 왔다. 지금까지 한 작업을 정리해보자.

- **Ver.1: 프론트 컨트롤러를 도입**
	- 기존 구조를 최대한 유지하면서 프론트 컨트롤러를 도입
	- 공통 처리를 해서 컨트롤러의 코드 간소화, 중복 제거
- **Ver.2**: View 분류
	- MyView 도입
	- 단순 반복 되는 뷰 로직 분리
- **Ver.3**: Model 추가
	- 서블릿 종속성 제거 (paramMap)
	- 뷰 이름 중복 제거 (viewResolver)
- **Ver.4**: 단순하고 실용적인 컨트롤러
	- Ver.3과 거의 비슷
	- 구현 입장에서 ModelView를 직접 생성해서 반환하지 않도록 편리한 인터페이스 제공
- **Ver.5**: 유연한 컨트롤러
	- 어댑터 도입
	- 어댑터를 추가해서 프레임워크를 유연하고 확장성 있게 설계

여기서 더 발전시키면 좋겠지만, 스프링 MVC의 핵심 구조를 파악하는데 필요한 부분은 모두 만들어보았다. 사실은 지금까지 작성한 코드는 스프링 MVC 프레임워크의 핵심 코드의 축약 버전이고, 구조도
거의 같다. 스프링 MVC는 지금까지 우리가 학습한 내용과 거의 같은 구조를 가지고 있다.