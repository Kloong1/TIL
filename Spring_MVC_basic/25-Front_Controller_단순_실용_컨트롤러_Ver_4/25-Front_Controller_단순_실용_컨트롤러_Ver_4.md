# Front Controller Ver. 4 - 단순하고 실용적인 컨트롤러

앞서 만든 v3 컨트롤러는 서블릿 종속성을 제거하고 뷰 경로의 중복을 제거하는 등, 잘 설계된 컨트롤러이다. 그런데 실제 컨트톨러 인터페이스를 구현하는 개발자 입장에서 보면, 항상 ModelView 객체를 생성하고 반환해야 하는 부분이 조금은 번거롭다.

**좋은 프레임워크는 아키텍처도 중요하지만, 그와 더불어 실제 개발하는 개발자가 단순하고 편리하게 사용할 수 있어야 한다. 소위 실용성이 있어야 한다**

## Ver.4 구조
![](스크린샷%202022-05-26%20오후%205.08.53.png)

기본적인 구조는 Ver.3과 같다. 대신 Controller가 `ModelView` 를 반환하지 않고, String 타입의 `viewName` 만 반환한다.


## Ver.4 구현

### Controller 구현
##### ControllerVer4.java
```Java
package com.kloong.servlet.web.frontcontroller.ver4;

import java.util.Map;

public interface ControllerVer4 {

    /**
     *
     * @param paramMap
     * @param model
     * @return viewName
     */
    String process(Map<String, String> paramMap, Map<String, Object> model);
}
```
- Ver.3과 달리 ModelView 객체가 아닌 String을 반환한다. 반환하는 String은 view의 논리 이름이다.
- Ver.3 까지는 컨트롤러가 ModelView 객체를 만들어서 멤버 변수인  `Map<String, Object> model`에 데이터를 저장했는데, 이제는 프론트 컨트롤러가 만들어서 넘겨주는 `Map<String, Object> model` 에 저장한다.
- 즉 이 Ver.4 프레임워크를 사용해서 컨트롤러를 개발하는 개발자는 `ModelView` 에 대해 알 필요도, 객체를 생성할 필요도 없다. 직관적으로 `model` 파라미터에 데이터를 저장하고, 단순히 String 타입의 viewName만 반환하면 된다.

##### MemberFormControllerVer4.java
```Java
package com.kloong.servlet.web.frontcontroller.ver4.controller;

import com.kloong.servlet.web.frontcontroller.ver4.ControllerVer4;

import java.util.Map;

public class MemberFormControllerVer4 implements ControllerVer4 {

    //Ver.3와 달리 컨트롤러를 개발하는 개발자 입장에서 ModelView 객체에 대해서 이해할 필요가 없다.
    //직관적으로 이해할 수 있는 model 변수를 사용하고, viewName만 반환하면 된다.
    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        return "new-form";
    }
}
```

단순히 view의 논리 이름만 반환하는 것을 확인할 수 있다.

##### MemberSaveControllerVer4.java
```Java
package com.kloong.servlet.web.frontcontroller.ver4.controller;

import com.kloong.servlet.domain.member.Member;
import com.kloong.servlet.domain.member.MemberRepository;
import com.kloong.servlet.web.frontcontroller.ver4.ControllerVer4;

import java.util.Map;

public class MemberSaveControllerVer4 implements ControllerVer4 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        model.put("member", member);

        return "save-result";
    }
}
```
- ModelView 객체를 직접 생성할 필요가 없다. 프론트 컨트롤러에서 넘겨주는 model 변수를 가져다 사용한다.

##### MemberListControllerVer4.java
```Java
package com.kloong.servlet.web.frontcontroller.ver4.controller;

import com.kloong.servlet.domain.member.Member;
import com.kloong.servlet.domain.member.MemberRepository;
import com.kloong.servlet.web.frontcontroller.ver4.ControllerVer4;

import java.util.List;
import java.util.Map;

public class MemberListControllerVer4 implements ControllerVer4 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        List<Member> members = memberRepository.findAll();
        model.put("members", members);
        return "members";
    }
}
```

### Front Controller 구현
##### FrontControllerServletVer4.java
```Java
package com.kloong.servlet.web.frontcontroller.ver4;

import ... //생략

// /front-controller/v4/ 의 모든 하위 경로 URL 요청이 이 서블릿으로 전달된다.
@WebServlet(name = "frontControllerServletVer4", urlPatterns = "/front-controller/v4/*")
public class FrontControllerServletVer4 extends HttpServlet {

    private Map<String, ControllerVer4> controllerMap = new HashMap<>();

    private static final String VIEW_PATH = "/WEB-INF/views/";
    private static final String VIEW_EXTENSION = ".jsp";

    public FrontControllerServletVer4() {
        controllerMap.put("/front-controller/v4/members/new-form", new MemberFormControllerVer4());
        controllerMap.put("/front-controller/v4/members/save", new MemberSaveControllerVer4());
        controllerMap.put("/front-controller/v4/members", new MemberListControllerVer4());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();//서버의 IP와 port 이후의 URI를 얻을 수 있음. 즉 생성자에서 mapping한 request URI를 얻을 수 있다.

        ControllerVer4 controller = controllerMap.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Map<String, String> paramMap = createParamMap(request);
        //ModelView를 사용하지 않음. 프론트 컨트롤러에서 직접 model을 만들어서 넘겨준다.
        Map<String, Object> model = new HashMap<>();
        String viewName = controller.process(paramMap, model);

        MyView view = viewResolver(viewName);
        view.render(model, request, response);
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
- `FrontControllerServletVer4` 는 Ver.3과 거의 동일하다.
- `Map<String, Object> model` 객체를 직접 만들어서 `controller.process(paramMap, model)` 이렇게 넘겨주는 것 정도의 코드가 추가되었다.
- 컨트롤러가 view의 논리 이름을 직접 반환한다. 이 값으로 `viewResolver()` 메소드를 호출하여 MyView 객체를 얻는다.


## 정리
이번 버전의 컨트롤러는 매우 단순하고 실용적이다. 기존 구조에서 모델을 파라미터로 넘기고, 뷰의 논리 이름을 반환한다는 작은 아이디어를 적용했을 뿐인데, 컨트롤러를 구현하는 개발자 입장에서 보면 이제 군더더기 없는 코드를 작성할 수 있다.

또한 중요한 사실은 여기까지 한번에 온 것이 아니라는 점이다. 프레임워크가 점진적으로 발전하는 과정
속에서 이런 방법도 찾을 수 있었다.

**프레임워크나 공통 기능을 개발하는 일이 수고로워야 사용하는 개발자가 편리해진다!**