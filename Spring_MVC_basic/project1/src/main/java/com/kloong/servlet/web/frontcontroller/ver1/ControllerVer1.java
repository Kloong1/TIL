package com.kloong.servlet.web.frontcontroller.ver1;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ControllerVer1 {
    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
