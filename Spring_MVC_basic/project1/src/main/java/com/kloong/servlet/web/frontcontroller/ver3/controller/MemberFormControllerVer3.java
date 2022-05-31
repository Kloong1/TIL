package com.kloong.servlet.web.frontcontroller.ver3.controller;

import com.kloong.servlet.web.frontcontroller.ModelView;
import com.kloong.servlet.web.frontcontroller.ver3.ControllerVer3;

import java.util.Map;

public class MemberFormControllerVer3 implements ControllerVer3 {

    @Override
    public ModelView process(Map<String, String> paramMap) {
        return new ModelView("new-form");
    }
}
