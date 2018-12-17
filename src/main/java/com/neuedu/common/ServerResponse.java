package com.neuedu.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.crypto.Data;
/**
 *服务器端返回到前端的高复用的响应对象
 **/
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse <T>{

    private int  status;  //返回到前端的状态码
    private  T   data;   //返回到前台的数据
    private  String msg;  //当status!=0,封装了错误信息

    private ServerResponse() {

    }


    private ServerResponse(int status ) {
        this.status = status;

    }


    private ServerResponse(int status, T data ) {
        this.status = status;
        this.data = data;

    }


    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }


    private ServerResponse(int status, T data, String msg) {
        this.status = status;
        this.data = data;
        this.msg = msg;
    }
/*
*调用接口成功时回调
 */
    public  static  ServerResponse serverResponseBySuccess(){
        return new ServerResponse(RespondseCode.SUCCESS);

    }
    public  static <T> ServerResponse serverResponseBySuccess(T data){
        return new ServerResponse(RespondseCode.SUCCESS,data);

    }
    public  static  <T> ServerResponse serverResponseBySuccess(T data,String msg){
        return new ServerResponse(RespondseCode.SUCCESS,data,msg);

    }
/*
*接口调用失败时回调
 */
public  static  ServerResponse  serverResponseByError() {

    return new ServerResponse(RespondseCode.ERROR);
}
    public  static  ServerResponse  serverResponseByError(String msg) {

        return  new ServerResponse(RespondseCode.ERROR,msg);
}
    public  static  ServerResponse  serverResponseByError(int status) {

        return  new ServerResponse(status);
    }

    public  static  ServerResponse  serverResponseByError(int status,String msg) {

        return  new ServerResponse(status,msg);
    }

/*
*判断接口是否正确返回
* status==0
 */
@JsonIgnore
public  boolean  isSuccess() {
    return this.status==RespondseCode.SUCCESS;
}

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ServerResponse{" +
                "status=" + status +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}';
    }

    public  static  void  main(String  [] args){
         ServerResponse serverresponse=new ServerResponse(0,new Object());
        ServerResponse serverresponse1=ServerResponse.serverResponseBySuccess("hello",null);

       // System.out.println(serverresponse);
        System.out.println(serverresponse1);


    }
}
