# Front Controller Ver. 2 - View 분리

모든 컨트롤러에 View로 이동하는 부분에 중복이 있어서 깔끔하지 않다.
```Java
String viewPath = "/WEB-INF/views/new-form.jsp";
RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
dispatcher.forward(request, response);
```

이 부분을 분리해서 View를 처리하는 객체를 만들자.


## Ver 2 구조
![](스크린샷%202022-05-25%20오후%203.45.25.png)
- Controller가 더이상 JSP forwarding을 하지 않는다.
- Controller는 비즈니스 로직을 수행한 뒤, MyView 객체를 만들어서 반환한다.
- Controller가 MyView 객체를 반환하면 Front Controller가 MyView 객체의 `render()`를 호출해서 forwarding을 한다.


## Ver 2 구현

### MyView 구현
뷰 객체는 이후 다른 버전에서 재활용 할 것이므로 패키지 위치를 frontcontroller에 두었다.

##### MyView.java
```Java
package com.kloong.servlet.web.frontcontroller;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyView {

    private String viewPath;

    public MyView(String viewPath) {
        this.viewPath = viewPath;
    }

    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```

RequestDispatcher가 Forwarding하는 코드가 MyView 객체에 있는 것을 확인할 수 있다.

>**참고**
>클래스 이름이 MyView 인 것은 Spring MVC에 이미 View라는 클래스가 존재하기 때문이다. 헷갈림 방지를 위한 것이므로 불편해도 어쩔 수 없음 ㅋㅋ

### Controller 구현

##### ControllerVer2.java
```Java
package com.kloong.servlet.web.frontcontroller.ver2;

import com.kloong.servlet.web.frontcontroller.MyView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ControllerVer2 {
    //Ver1과 다른 점은 반환형이 void에서 MyView로 바뀐 것
    MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
```

(프론트 컨트롤러를 제외한) 모든 컨트롤러는 이 인터페이스를 구현해야 한다. Ver1과 달라진 점은 `process()` 가 MyView 객체를 반환해야 한다는 것이다.

##### MemberFormControllerVer2.java
```Java
package com.kloong.servlet.web.frontcontroller.ver2.controller;

import com.kloong.servlet.web.frontcontroller.MyView;
import com.kloong.servlet.web.frontcontroller.ver2.ControllerVer2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberFormControllerVer2 implements ControllerVer2 {
    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return new MyView("/WEB-INF/views/new-form.jsp");
    }
}
```

비즈니스 로직이 없는 MemberFormController의 경우에는 MyView 객체만 만들어서 반환하면 끝난다. Forwarding 하는 중복 코드가 사라져서 매우 깔끔해진 것을 볼 수 있다.

##### MemberSaveControllerVer2.java
```Java
package com.kloong.servlet.web.frontcontroller.ver2.controller;

import com.kloong.servlet.domain.member.Member;
import com.kloong.servlet.domain.member.MemberRepository;
import com.kloong.servlet.web.frontcontroller.MyView;
import com.kloong.servlet.web.frontcontroller.ver2.ControllerVer2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberSaveControllerVer2 implements ControllerVer2 {
    private MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        //Model에 데이터 보관
        request.setAttribute("member", member);

        return new MyView("/WEB-INF/views/save-result.jsp");
    }
}
```

##### MemberListControllerVer2.java
```Java
package com.kloong.servlet.web.frontcontroller.ver2.controller;

import com.kloong.servlet.domain.member.Member;
import com.kloong.servlet.domain.member.MemberRepository;
import com.kloong.servlet.web.frontcontroller.MyView;
import com.kloong.servlet.web.frontcontroller.ver2.ControllerVer2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MemberListControllerVer2 implements ControllerVer2 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Member> members = memberRepository.findAll();
        request.setAttribute("members", members);
        return new MyView("/WEB-INF/views/members.jsp");
    }
}
```

### Front Controller 구현

##### FrontControllerServletVer2.java
```Java
package com.kloong.servlet.web.frontcontroller.ver2;

import com.kloong.servlet.web.frontcontroller.MyView;
import com.kloong.servlet.web.frontcontroller.ver1.ControllerVer1;
import com.kloong.servlet.web.frontcontroller.ver2.controller.MemberFormControllerVer2;
import com.kloong.servlet.web.frontcontroller.ver2.controller.MemberListControllerVer2;
import com.kloong.servlet.web.frontcontroller.ver2.controller.MemberSaveControllerVer2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// /front-controller/v2/ 의 모든 하위 경로 URL 요청이 이 서블릿으로 전달된다.
@WebServlet(name = "frontControllerServletVer2", urlPatterns = "/front-controller/v2/*")
public class FrontControllerServletVer2 extends HttpServlet {

    private Map<String, ControllerVer2> controllerMap = new HashMap<>();

    public FrontControllerServletVer2() {
        controllerMap.put("/front-controller/v2/members/new-form", new MemberFormControllerVer2());
        controllerMap.put("/front-controller/v2/members/save", new MemberSaveControllerVer2());
        controllerMap.put("/front-controller/v2/members", new MemberListControllerVer2());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();//서버의 IP와 port 이후의 URI를 얻을 수 있음. 즉 생성자에서 mapping한 request URI를 얻을 수 있다.

        ControllerVer2 controller = controllerMap.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyView view = controller.process(request, response);
        view.render(request, response);
    }
}
```
- `controller.process()`가 이제 MyView 객체를 반환한다.
- 프론트 컨트롤러는 반환 받은 MyView 객체의 `render()` 메소드를 호출한다.
- 이로 인해 forwarding 하는 부분을 프론트 컨트롤러에서 공통 처리 할 수 있게 되었다.


## 마치며
- View를 성공적으로 분리해냈다.
	- 공통 처리가 가능해져서 중복 코드가 사라졌다.
- 그러나 아직 Model이 명확하게 없다.
- 또 MemberFormControllerVer2 같은 경우에는 `HttpServletRequest`, `HttpServletResponse` 객체를 사용하지 않음에도 불구하고 해당 객체를 전달받는다.
- 이후 버전에서 이런 부분을 개선해 나갈 것이다.

