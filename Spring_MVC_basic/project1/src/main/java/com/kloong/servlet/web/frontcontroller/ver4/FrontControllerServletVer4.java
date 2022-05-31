package com.kloong.servlet.web.frontcontroller.ver4;

import com.kloong.servlet.web.frontcontroller.ModelView;
import com.kloong.servlet.web.frontcontroller.MyView;
import com.kloong.servlet.web.frontcontroller.ver4.controller.MemberFormControllerVer4;
import com.kloong.servlet.web.frontcontroller.ver4.controller.MemberListControllerVer4;
import com.kloong.servlet.web.frontcontroller.ver4.controller.MemberSaveControllerVer4;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
