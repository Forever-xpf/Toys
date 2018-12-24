package com.neuedu.controller.common.intercepter;

import com.google.gson.Gson;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

public class AuthorityInterceptorLogin implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o) throws Exception {

        HttpSession session=httpServletRequest.getSession();
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENTUSER);
        //重构HttpServerletResponse

        if(userInfo==null){
            //未登录
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter printWriter=response.getWriter();
                ServerResponse serverResponse=ServerResponse.serverResponseByError("需要登录");
                Gson gson=new Gson();
                String json=gson.toJson(serverResponse);
                printWriter.write(json);
                printWriter.flush();
                printWriter.close();
                return false;
            }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

        System.out.println("=========该请求被处理=========");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        System.out.println("=========请求处理成功=========");
    }
}
