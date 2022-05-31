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
