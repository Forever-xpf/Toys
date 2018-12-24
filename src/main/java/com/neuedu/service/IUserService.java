package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;

public interface IUserService {

    /*
    *登录接口
     */

    ServerResponse  login(String username,String password);

    /*
     *注册接口
     */

    ServerResponse  register(UserInfo userInfo);

    /*
     *根据密保问题找回密码接口
     */
    ServerResponse forget_Get_Question(String username);
/*
*检查问题答案
 */

    ServerResponse forget_Check_Answer(String username,String question,String answer);

    /*
     *重置密码接口
     *
     */

    ServerResponse forget_Reset_Password(String username,String passwordNew,String forgetToken);

    /*
     *检查用户名或者邮箱是否有效接口
     *
     */

    ServerResponse check_valid(String str,String type);

    /*
     *登录状态重置密码
     *
     */

    ServerResponse reset_password(String username, String passwordOld,String passwordNew);

    /*
     *登录状态更新用户信息
     *
     */
    ServerResponse update_information(UserInfo user);


    /*
     *根据userid获取到用户最新的信息
     *
     */

UserInfo findUserInfoByUserId(Integer userId);

/**
 *
 * 保护用户token信息
 */
int updateTokenByUserId(Integer userId,String token);

/**
 * 根据token查询用户信息
 */
UserInfo findUserInfoByToken(String token);

}
