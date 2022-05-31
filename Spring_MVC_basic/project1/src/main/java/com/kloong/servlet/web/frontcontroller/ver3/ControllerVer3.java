package com.kloong.servlet.web.frontcontroller.ver3;

import com.kloong.servlet.web.frontcontroller.ModelView;

import java.util.Map;

public interface ControllerVer3 {
    //Servlet 기술에 종속적이지 않음.
    ModelView process(Map<String, String> paramMap);
}
