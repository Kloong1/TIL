## Front Controller 도입 - Ver. 1

프론트 컨트롤러를 단계적으로 도입해보자. 목표는 기존 코드를 최대한 유지하면서, 프론트 컨트롤러를 도입하는 것이다.

먼저 구조를 맞추어두고 점진적으로 리펙터링 해보자.


## Ver.1 구조
![](스크린샷%202022-05-25%20오후%202.51.34.png)
- Front Controller가 클라이언트의 HTTP 요청을 받는다.
- Front Controller가 갖고있는 URL 매핑 정보를 통해서 HTTP 요청 URL에 맞는 컨트롤러를 찾아서 호출한다.
- 컨트롤러는 비즈니스 로직을 수행하고, JSP forward한다.


## Ver.1 구현

### 컨트롤러 구현
서블릿과 비슷한 모양의 컨트롤러 인터페이스를 도입한다. 각 컨트롤러들은 이 인터페이스를 구현하면 된다. 프론트 컨트롤러는 이 인터페이스를 호출해서 구현과 관계없이 로직의 일관성을 가져갈 수 있다.

##### ControllerVer1.java
```Java
package com.kloong.servlet.web.frontcontroller.ver1;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ControllerVer1 {
    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
```

이제 이 인터페이스를 구현한 컨트롤러를 만들어보자. 지금 단계에서는 기존 로직을 최대한 유지하는게
핵심이다.

##### MemberFormControllerVer1.java
```Java
package com.kloong.servlet.web.frontcontroller.ver1.controller;

import com.kloong.servlet.web.frontcontroller.ver1.ControllerVer1;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberFormControllerVer1 implements ControllerVer1 {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String viewPath = "/WEB-INF/views/new-form.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```

##### MemberSaveControllerVer1.java
```Java
package com.kloong.servlet.web.frontcontroller.ver1.controller;

import com.kloong.servlet.domain.member.Member;
import com.kloong.servlet.domain.member.MemberRepository;
import com.kloong.servlet.web.frontcontroller.ver1.ControllerVer1;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberSaveControllerVer1 implements ControllerVer1 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        //Model에 데이터 보관
        request.setAttribute("member", member);

        String viewPath = "/WEB-INF/views/save-result.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```

##### MemberListControllerVer1.java
```Java
package com.kloong.servlet.web.frontcontroller.ver1.controller;

import com.kloong.servlet.domain.member.Member;
import com.kloong.servlet.domain.member.MemberRepository;
import com.kloong.servlet.web.frontcontroller.ver1.ControllerVer1;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MemberListControllerVer1 implements ControllerVer1 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Member> members = memberRepository.findAll();

        request.setAttribute("members", members);

        String viewPath = "/WEB-INF/views/members.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```

세 컨트롤러 모두 ControllerVer1을 구현했고, 기존에 작성해 뒀던 서블릿의 `service()` 코드를 그대로 가져왔다. 이는 추후에 개선해 나갈 것이다.

### 프론트 컨트롤러 구현
이제 프론트 컨트롤러를 만들어보자. 프론트 컨트롤러는 HTTP 요청을 받아야 하므로 HttpServlet을 상속받아야한다.

##### FrontControllerServlet.java
```Java
package com.kloong.servlet.web.frontcontroller.ver1;

import com.kloong.servlet.web.frontcontroller.ver1.controller.MemberFormControllerVer1;
import com.kloong.servlet.web.frontcontroller.ver1.controller.MemberListControllerVer1;
import com.kloong.servlet.web.frontcontroller.ver1.controller.MemberSaveControllerVer1;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// /front-controller/v1/ 의 모든 하위 경로 URL 요청이 이 서블릿으로 전달된다.
@WebServlet(name = "frontControllerServletVer1", urlPatterns = "/front-controller/v1/*")
public class FrontControllerServletVer1 extends HttpServlet {

    private Map<String, ControllerVer1> controllerMap = new HashMap<>();

    public FrontControllerServletVer1() {
        controllerMap.put("/front-controller/v1/members/new-form", new MemberFormControllerVer1());
        controllerMap.put("/front-controller/v1/members/save", new MemberSaveControllerVer1());
        controllerMap.put("/front-controller/v1/members", new MemberListControllerVer1());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();//서버의 IP와 port 이후의 URI를 얻을 수 있음. 즉 생성자에서 mapping한 request URI를 얻을 수 있다.

        ControllerVer1 controller = controllerMap.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        controller.process(request, response);
    }
}
```

#### 프론트 컨트롤러 분석
##### urlPattern
- `urlPatterns = "/front-controller/v1/*"` : `/front-controller/v1 `를 포함한 하위 모든 요청은 이 서블릿에서 받는다.
- 예) `/front-controller/v1`, `/front-controller/v1/members`, `/front-controller/members/new-form`

##### controllerMap
- request URI와 컨트롤러를 mapping해주는 역할을 한다.
- Key: mapping URL
- Value: 호출될 컨트롤러

##### service() 메소드
- `request.getRequestURI()` 메소드로 HTTP 요청 메시지에서 request URI를 얻어낸다.
- 그 다음 controllerMap에서 해당 request URI를 조회해서 호출한 컨트롤러를 찾는다.
	- 만약 해당 URI와 mapping된 컨트롤러가 없다면 404 코드를 반환한다.
- 찾은 컨트롤러의 `process()` 메소드를 호출한다.