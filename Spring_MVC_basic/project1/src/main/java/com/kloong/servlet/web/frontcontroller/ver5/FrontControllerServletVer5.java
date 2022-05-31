package com.kloong.servlet.web.frontcontroller.ver5;

import com.kloong.servlet.web.frontcontroller.ModelView;
import com.kloong.servlet.web.frontcontroller.MyView;
import com.kloong.servlet.web.frontcontroller.ver3.controller.MemberFormControllerVer3;
import com.kloong.servlet.web.frontcontroller.ver3.controller.MemberListControllerVer3;
import com.kloong.servlet.web.frontcontroller.ver3.controller.MemberSaveControllerVer3;
import com.kloong.servlet.web.frontcontroller.ver4.controller.MemberFormControllerVer4;
import com.kloong.servlet.web.frontcontroller.ver4.controller.MemberListControllerVer4;
import com.kloong.servlet.web.frontcontroller.ver4.controller.MemberSaveControllerVer4;
import com.kloong.servlet.web.frontcontroller.ver5.adapter.ControllerVer3HandlerAdapter;
import com.kloong.servlet.web.frontcontroller.ver5.adapter.ControllerVer4HandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerVer3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerVer3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerVer3());

        handlerMappingMap.put("/front-controller/v5/v4/members/new-form", new MemberFormControllerVer4());
        handlerMappingMap.put("/front-controller/v5/v4/members/save", new MemberSaveControllerVer4());
        handlerMappingMap.put("/front-controller/v5/v4/members", new MemberListControllerVer4());
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
