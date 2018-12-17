package com.neuedu.service.impl;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.dao.UserInfoMapper;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import com.neuedu.utils.MD5Utils;
import com.neuedu.utils.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Override
    /*
    *登录
     */
    public ServerResponse login(String username, String password) {
       //step1:参数的非空校验
          if(username==null || username.equals("")) {

              return ServerResponse.serverResponseByError("用户名不能为空！！！");
          }
        if(password==null || password.equals("")) {

            return ServerResponse.serverResponseByError("密码不能为空！！！");
        }
        //step2:检查用户名是否存在
    int  result=userInfoMapper.checkUsername(username);
          if(result==0){
              return ServerResponse.serverResponseByError("用户名不存在！！！");
          }
        //step3:根据用户名和密码查找用户信息
            UserInfo userInfo=userInfoMapper.selectUserInfoByUsernameAndPassword(username,MD5Utils.getMD5Code(password));
           if(userInfo==null){
               return ServerResponse.serverResponseByError("密码错误");
           }
        //step4:返回结果
       userInfo.setPassword("");
        return ServerResponse.serverResponseBySuccess(userInfo);



    }
    /*
     *注册
     */
    @Override
    public ServerResponse register(UserInfo userInfo) {

        //step1:参数的非空校验
       if(userInfo==null){
           return ServerResponse.serverResponseByError("参数必需");
       }

        //step2:校验用户名
        int  result=userInfoMapper.checkUsername(userInfo.getUsername());
        if(result>0){
            return ServerResponse.serverResponseByError("用户名已经存在！！！");
        }


        //step3:校验邮箱
        int  result_email=userInfoMapper.checkEmail(userInfo.getEmail());
        if(result_email>0){
            return ServerResponse.serverResponseByError("邮箱已经存在！！！");
        }
        //step4:注册
        userInfo.setRole(Const.RoleEnum.ROLE_CUSTOMER.getCode());
        //MD5加密
        userInfo.setPassword(MD5Utils.getMD5Code(userInfo.getPassword()));
        int count=userInfoMapper.insert(userInfo);
        if(count>0){
            return ServerResponse.serverResponseBySuccess("注册成功");
        }
        //step5:返回结果
        return ServerResponse.serverResponseByError("注册失败");



    }
    /*
     *获得问题
     */
    @Override
    public ServerResponse forget_Get_Question(String username) {
        //step1:参数校验
        if(username==null || username.equals("")) {
            return ServerResponse.serverResponseByError("用户名不能为空！！！");
        }


        //step2:参数username
        int  result=userInfoMapper.checkUsername(username);
        if(result==0){
            return ServerResponse.serverResponseByError("用户名不存在,请重新输入！");
        }

        //step3:查找密保问题
    String question= userInfoMapper.selectQuestionByUsername(username);
if(question==null || question.equals("")){
    return ServerResponse.serverResponseByError("密保问题为空！");
}

        return ServerResponse.serverResponseBySuccess(question);
    }
    /*
     *检查密保问题答案
     */
    @Override
    public ServerResponse forget_Check_Answer(String username, String question, String answer) {
       //step1:参数校验
        if(username==null || username.equals("")) {
            return ServerResponse.serverResponseByError("用户名不能为空！！！");
        }
        if(question==null || question.equals("")) {
            return ServerResponse.serverResponseByError("问题不能为空！！！");
        }
        if(answer==null || answer.equals("")) {
            return ServerResponse.serverResponseByError("答案不能为空！！！");
        }

        //step2:根据username,question,answer查询
    int result=userInfoMapper.selectByUsernameAndQuestionAndAnswer(username,question,answer);
   if(result==0){
       //答案错误
       return ServerResponse.serverResponseByError("答案错误");
   }
        //step3:服务端生成一个 token保存并将token返回给客户端
String  forgetToken= UUID.randomUUID().toString();
   //guava  cache
        TokenCache.set(username,forgetToken);
        return ServerResponse.serverResponseBySuccess(forgetToken);
    }

    /*
     *忘记密码之重置密码密码
     */
    @Override
    public ServerResponse forget_Reset_Password(String username, String passwordNew, String forgetToken) {
      // step1:参数校验
        if(username==null || username.equals("")) {
            return ServerResponse.serverResponseByError("用户名不能为空！！！");
        }
        if(passwordNew==null || passwordNew.equals("")) {
            return ServerResponse.serverResponseByError("问题不能为空！！！");
        }
        if(forgetToken==null || forgetToken.equals("")) {
            return ServerResponse.serverResponseByError("token不能为空！！！");
        }

      //step2:token校验
String token=TokenCache.get(username);
        if(token==null){

return ServerResponse.serverResponseByError("token过期");
        }
        if(!token.equals(forgetToken)){
            return ServerResponse.serverResponseByError("无效的token");
        }
        //step3:重置密码
       int result= userInfoMapper.updateUserPassword(username,passwordNew);
if(result>0){
    return ServerResponse.serverResponseBySuccess();
}
        return ServerResponse.serverResponseByError("密码修改失败");
    }

    @Override
    public ServerResponse check_valid(String str, String type) {
            //step1:参数非空校验
        if(str==null || str.equals("")) {
            return ServerResponse.serverResponseByError("用户名或者邮箱不能为空！！！");
        }
        if(type==null || type.equals("")) {
            return ServerResponse.serverResponseByError("检验的参数类型不能为空！！！");
        }

        //step2:type:username -->校验用户名    //step3:返回结果
        //        email-->校验邮箱
    if(type.equals("username")){
          int result=  userInfoMapper.checkUsername(str);
          if(result>0){
              //用户已存在
              return  ServerResponse.serverResponseByError("用户已经存在");
          }else{
              return ServerResponse.serverResponseBySuccess();
          }
    }else if(type.equals("email")){
            int result=userInfoMapper.checkEmail(str);
        if(result>0){
            //邮箱已存在
            return  ServerResponse.serverResponseByError("用户已经存在");
        }else{
            return ServerResponse.serverResponseBySuccess();
        }
    }else{
            return ServerResponse.serverResponseByError("参数类型错误");
    }




    }
    /*
    *用户密码重置
     */
    @Override
    public ServerResponse reset_password(String username,String passwordOld, String passwordNew) {
       //step1:参数校验
        if(username==null || username.equals("")) {
            return ServerResponse.serverResponseByError("用户名不能为空！！！");
        }
        if(passwordOld==null || passwordOld.equals("")) {
            return ServerResponse.serverResponseByError("旧密码不能为空！！！");
        }
        if(passwordNew==null || passwordNew.equals("")) {
            return ServerResponse.serverResponseByError("新密码不能为空！！！");
        }
    //step2:根据用户名和旧密码
UserInfo userInfo=userInfoMapper.selectUserInfoByUsernameAndPassword(username,MD5Utils.getMD5Code(passwordOld));
        if(userInfo==null){
            return  ServerResponse.serverResponseByError("旧密码错误");
        }
        //step1:修改新密码
        userInfo.setPassword(MD5Utils.getMD5Code(passwordNew));
int result=userInfoMapper.updateByPrimaryKey(userInfo);
if(result>0){
    return ServerResponse.serverResponseBySuccess("密码修改成功");
}
        return ServerResponse.serverResponseByError("密码修改失败");
    }
/*
*更新用户信息
 */
    @Override
    public ServerResponse update_information(UserInfo user) {
       //step1:参数校验
        if(user==null) {
            return ServerResponse.serverResponseByError("参数不能为空！！！");
        }
        //step2:更新用户信息
      int result=  userInfoMapper.updateUserBySelectActive(user);
        if(result>0){
            return ServerResponse.serverResponseBySuccess("用户个人信息修改成功");
        }
        return ServerResponse.serverResponseByError("个人信息更新失败");
    }
/*
*查询用户信息
 */
    @Override
    public UserInfo findUserInfoByUserId(Integer userId) {
        return userInfoMapper.selectByPrimaryKey(userId);

    }




}