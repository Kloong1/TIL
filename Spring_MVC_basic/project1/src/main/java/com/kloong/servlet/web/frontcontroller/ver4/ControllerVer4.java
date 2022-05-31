package com.kloong.servlet.web.frontcontroller.ver4;

import java.util.Map;

public interface ControllerVer4 {

    /**
     *
     * @param paramMap
     * @param model
     * @return viewName
     */
    String process(Map<String, String> paramMap, Map<String, Object> model);
}
