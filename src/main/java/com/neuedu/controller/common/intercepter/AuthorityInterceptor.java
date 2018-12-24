package com.neuedu.controller.common.intercepter;

import com.google.gson.Gson;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import com.neuedu.utils.JsonUtils;
import com.neuedu.utils.RedisPoolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.logging.Handler;

public class AuthorityInterceptor implements HandlerInterceptor {

    @Autowired
    IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse response, Object handler) throws Exception {

        //拦截某一个方法
        HandlerMethod handlerMethod=(HandlerMethod)handler;
        String classname=handlerMethod.getBean().getClass().getSimpleName();
        String methodName=handlerMethod.getMethod().getName();
        /*if(classname.equals("ProductManageController")&&methodName.equals("方法名")){
            return true;
        }*/



        HttpSession session=httpServletRequest.getSession();
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENTUSER);

        if(userInfo==null){
            //从cookie中获取token信息
            Cookie[] cookies=httpServletRequest.getCookies();
            if(cookies!=null&&cookies.length>0){
            for(Cookie cookie:cookies){
                String cookieName=cookie.getName();
                if(cookieName.equals(Const.AUTOLOGINTOKEN)){
                    String autoLoginToken=cookie.getValue();
                    //根据token查询用户信息
                    userInfo=userService.findUserInfoByToken(autoLoginToken);
                    if(userInfo!=null){
                        session.setAttribute(Const.CURRENTUSER,userInfo);
                        //String userJson=JsonUtils.obj2StringPretty(userInfo);
                        //RedisPoolUtils.set(session.getId(),userJson);
                    }
                    break;
                }
            }
            }
        }
        //重构HttpServerletResponse

        if(userInfo==null||userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter printWriter=response.getWriter();

            if(userInfo==null){
                //未登录
                ServerResponse serverResponse=ServerResponse.serverResponseByError("需要登录");
                Gson gson=new Gson();
                String json=gson.toJson(serverResponse);
                printWriter.write(json);

            }
            //判断用户权限
          else{
                //无权限操作
                ServerResponse serverResponse=ServerResponse.serverResponseByError("该用户无权限");
                Gson gson=new Gson();
                String json=gson.toJson(serverResponse);
                printWriter.write(json);

            }
            printWriter.flush();
            printWriter.close();
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
      System.out.println("==========postHandle============");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        System.out.println("==========afterCompletion============");
    }
}
