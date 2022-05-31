package com.kloong.servlet.web.frontcontroller.ver2;

import com.kloong.servlet.web.frontcontroller.MyView;
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
