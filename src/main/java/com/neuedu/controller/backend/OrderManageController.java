package com.neuedu.controller.backend;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/manage/order")
public class OrderManageController {
@Autowired
    IOrderService orderService;
    /*
     *获取订单列表
     */
    @RequestMapping(value = "/list.do")
    ServerResponse list(HttpSession session,
                        @RequestParam(required = false,defaultValue = "1")Integer pageNum,
                        @RequestParam(required = false,defaultValue = "10")Integer pageSize){

        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENTUSER);

        if(userInfo==null){
            return ServerResponse.serverResponseByError("需要登录");
        }
        //判断用户权限
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.serverResponseByError(Const.ReponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ReponseCodeEnum.NO_PRIVILEGE.getDesc());
        }
        return orderService.list(userInfo.getId(),pageNum,pageSize);
    }
    /*
     *获取订单详情
     */
    @RequestMapping(value = "/detail.do")
    ServerResponse detail(HttpSession session,Long orderNo){

        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENTUSER);

        if(userInfo==null){
            return ServerResponse.serverResponseByError("需要登录");
        }
        //判断用户权限
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.serverResponseByError(Const.ReponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ReponseCodeEnum.NO_PRIVILEGE.getDesc());
        }
        return orderService.detail(orderNo);
    }

    /*
     *后台—订单发货
     */
    @RequestMapping(value = "/send_goods.do")
    ServerResponse send_goods(HttpSession session,Long orderNo){

        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENTUSER);

        if(userInfo==null){
            return ServerResponse.serverResponseByError("需要登录");
        }
        //判断用户权限
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.serverResponseByError(Const.ReponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ReponseCodeEnum.NO_PRIVILEGE.getDesc());
        }
        return orderService.send_goods(orderNo);
    }





}
