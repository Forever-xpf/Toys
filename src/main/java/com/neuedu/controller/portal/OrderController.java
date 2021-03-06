package com.neuedu.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping(value = "/order")
public class OrderController {
    @Autowired
IOrderService orderService;
    /*
    *创建订单
     */
    @RequestMapping(value = "/create.do")
    ServerResponse createOrder(HttpSession session,Integer shippingId){

        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENTUSER);
        return orderService.createOrder(userInfo.getId(),shippingId);
    }

    /*
     *取消订单
     */
    @RequestMapping(value = "/cancel.do")
    ServerResponse cancel(HttpSession session,Long orderNo){

        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENTUSER);
        return orderService.cancel(userInfo.getId(),orderNo);
    }

    /*
     *获取订单的商品信息
     */
    @RequestMapping(value = "/get_order_cart_product.do")
    ServerResponse get_order_cart_product(HttpSession session){

        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENTUSER);
        return orderService.get_order_cart_product(userInfo.getId());
    }
    /*
     *获取订单列表
     */
    @RequestMapping(value = "/list.do")
    ServerResponse list(HttpSession session,
                        @RequestParam(required = false,defaultValue = "1")Integer pageNum,
                        @RequestParam(required = false,defaultValue = "10")Integer pageSize){

        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENTUSER);
        return orderService.list(userInfo.getId(),pageNum,pageSize);
    }
    /*
     *获取订单详情
     */
    @RequestMapping(value = "/detail.do")
    ServerResponse detail(HttpSession session,Long orderNo){

        return orderService.detail(orderNo);
    }

    /*
     *支付宝支付接口
     */
    @RequestMapping(value = "/pay.do")
    ServerResponse pay(HttpSession session,Long orderNo){

        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENTUSER);
        return orderService.pay(userInfo.getId(),orderNo);
    }

   /*
   *支付宝服务器调用商家服务器接口
    */
   @RequestMapping(value = "/alipay_callback.do")
   public  ServerResponse callback(HttpServletRequest request){

       System.out.println("支付宝服务器调用商家服务器接口");

       Map<String,String[]> params=request.getParameterMap();
       Map<String,String>  requestparams= Maps.newHashMap();
       Iterator<String> it=params.keySet().iterator();
       while (it.hasNext()){
           String key=it.next();
           String[] strArr=params.get(key);
           String value="";
           for(int i=0;i<strArr.length;i++){
               value=(i==strArr.length-1)?value+strArr[i]:value+strArr[i]+",";
           }
           requestparams.put(key,value);
       }
       //step1:支付宝验签
       try {
           requestparams.remove("sign_type");
          boolean result= AlipaySignature.rsaCheckV2(requestparams, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
          if(!result){
              return  ServerResponse.serverResponseByError("非法请求，验证不通过");
          }


       } catch (AlipayApiException e) {
           e.printStackTrace();
       }
        //业务逻辑处理
       return orderService.aplipay_callback(requestparams);
   }

    /*
     *查询订单的支付状态
     */
    @RequestMapping(value = "/query_order_pay_status.do")
    ServerResponse query_order_pay_status(HttpSession session,Long orderNo){

        return orderService.query_order_pay_status(orderNo);
    }


}
