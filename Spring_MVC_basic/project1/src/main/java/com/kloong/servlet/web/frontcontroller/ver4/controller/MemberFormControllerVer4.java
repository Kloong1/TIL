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
