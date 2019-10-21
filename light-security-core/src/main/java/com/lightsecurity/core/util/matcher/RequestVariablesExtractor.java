package com.lightsecurity.core.util.matcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用于从{@link javax.servlet.http.HttpServletRequest}提取URI变量的接口。
 */
public interface RequestVariablesExtractor {

    /**
     * 从请求中提取URL模板变量。
     * @param request
     * @return
     */
    Map<String, String> extractUriTemplateVariables(HttpServletRequest request);

}
