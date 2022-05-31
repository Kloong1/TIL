package com.kloong.servlet.web.frontcontroller.ver5.adapter;

import com.kloong.servlet.web.frontcontroller.ModelView;
import com.kloong.servlet.web.frontcontroller.ver3.ControllerVer3;
import com.kloong.servlet.web.frontcontroller.ver5.MyHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
