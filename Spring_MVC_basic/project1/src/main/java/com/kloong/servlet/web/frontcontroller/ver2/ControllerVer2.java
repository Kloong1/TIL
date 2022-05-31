package com.kloong.servlet.web.frontcontroller.ver2;

import com.kloong.servlet.web.frontcontroller.MyView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ControllerVer2 {
    //Ver1과 다른 점은 반환형이 void에서 MyView로 바뀐 것
    MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
