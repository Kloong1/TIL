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
