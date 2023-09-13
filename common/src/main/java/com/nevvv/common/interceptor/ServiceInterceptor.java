package com.nevvv.common.interceptor;

import com.nevvv.utils.shareData.BaseContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ServiceInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String loginId = request.getHeader("loginId");
        if (StringUtils.isNotEmpty(loginId)) {
            BaseContext.setThreadId(Long.valueOf(loginId));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //响应之前移除thread手动添加的内容
        BaseContext.removeThreadId();
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
