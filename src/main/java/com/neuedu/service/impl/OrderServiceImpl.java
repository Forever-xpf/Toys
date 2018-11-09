package com.neuedu.service.impl;

import com.google.common.collect.Lists;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.*;
import com.neuedu.pojo.*;
import com.neuedu.service.IOrderService;
import com.neuedu.utils.BigDecimalUtils;
import com.neuedu.utils.DateUtils;
import com.neuedu.utils.PropertiesUtils;
import com.neuedu.vo.OrderItemVO;
import com.neuedu.vo.OrderVO;
import com.neuedu.vo.ShippingVO;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    ShippingMapper shippingMapper;

    /*
    *创建订单
     */
    public ServerResponse createOrder(Integer userId, Integer shippingId) {

        //step1:非空校验
            if(shippingId==null){
                return ServerResponse.serverResponseByError("参数不能为空");
            }
        //step2:根据userId查询查询购物车中已选中的商品 ---》List<cart>
            List<Cart> cartList=cartMapper.findCartListByUserIdAndChecked(userId);
        //step3:List<cart>-->List<OrderItem>
            ServerResponse serverResponse=getCartOrderItem(userId,cartList);
            if(!serverResponse.isSuccess()){
                return  serverResponse;
            }

        //step4:创建订单order并将其保存到数据库
        //计算订单价格
        BigDecimal orderTotalPrice=new BigDecimal("0");
            List<OrderItem>  orderItemList=(List<OrderItem>)serverResponse.getData();
            if(orderItemList==null||orderItemList.size()==0){
                return ServerResponse.serverResponseByError("购物车为空");
            }
            orderTotalPrice=getOrderPrice(orderItemList);
         //添加有效的收货信息
        Shipping shipping=  shippingMapper.selectByPrimaryKey(shippingId);
        if(shipping==null){
            return ServerResponse.serverResponseByError("该收货地址不存在");
        }

         Order order= createOrder(userId,shippingId,orderTotalPrice);
         if(order==null){
             return ServerResponse.serverResponseByError("订单创建失败");
         }
        //step5:将List<OrderItem>保存到数据库

        for(OrderItem orderItem:orderItemList){
             orderItem.setOrderNo(order.getOrderNo());
        }
        //批量插入

        orderItemMapper.insertBatch(orderItemList);
        //step6:扣库存
        reduceProductStock(orderItemList);
        //step7:购物车中清空已下单的商品
          cleanCart(cartList);
        //step8:返回：OrderVO
      OrderVO orderVO= assembleOrderVO(order,orderItemList,shippingId);

        return ServerResponse.serverResponseBySuccess(orderVO);
    }

    /*
    *取消订单
     */
    @RequestMapping(value = "/cancel.do")
    @Override
    public ServerResponse cancel(Integer userId, Long orderNo) {
        //step1:参数校验
        if(orderNo==null){
            return ServerResponse.serverResponseByError("参数不能为空");
        }
        //step2:查询订单
       Order order= orderMapper.findOrderByUserIdAndOrderId(userId,orderNo);
        if(order==null ){
            return ServerResponse.serverResponseByError("订单不存在");
        }
        //step3:判断订单状态并取消
        if(order.getStatus()!=Const.OrderStatusEnum.ORDER_UN_PAY.getCode()){
            return ServerResponse.serverResponseByError("订单无法取消");
        }
        order.setStatus(Const.OrderStatusEnum.ORDER_CANCELED.getCode());
        int result=orderMapper.updateByPrimaryKey(order);
        //增加库存
        addProductStock(orderNo);
        //step4:返回结果
        if(result>0){
            return  ServerResponse.serverResponseBySuccess();
        }
        return ServerResponse.serverResponseByError("订单修改失败");
    }
    /*
     *获取订单的商品信息
     */
    
    @Override
    public ServerResponse get_order_cart_product(Integer userId) {
        return null;
    }


    private OrderVO assembleOrderVO(Order order,List<OrderItem> orderItemList,Integer shippingId){
     OrderVO orderVO=new OrderVO();
     List<OrderItemVO> orderItemVOList=Lists.newArrayList();
     for(OrderItem orderItem:orderItemList){
         OrderItemVO orderItemVO=assembleOrderItemVO(orderItem);
         orderItemVOList.add(orderItemVO);
     }
     orderVO.setOrderItemVOList(orderItemVOList);
     orderVO.setImageHost(PropertiesUtils.readByKey("imageHost"));
     //添加收货地址信息
        Shipping shipping=  shippingMapper.selectByPrimaryKey(shippingId);
    orderVO.setShippingId(shippingId);
      ShippingVO shippingVO=  assembleShippingVO(shipping);
      orderVO.setShippingVo(shippingVO);
      orderVO.setReceiverName(shipping.getReceiverName());

orderVO.setStatus(order.getStatus());
Const.OrderStatusEnum orderStatusEnum=Const.OrderStatusEnum.codeof(order.getStatus());
if(orderStatusEnum!=null){
    orderVO.setStatusDesc(orderStatusEnum.getDesc());
}
orderVO.setPostage(0);
orderVO.setPayment(order.getPayment());
orderVO.setPaymentType(order.getPaymentType());
Const.PaymentEnum paymentEnum=Const.PaymentEnum.codeof(order.getPaymentType());
if(paymentEnum!=null){

    orderVO.setPaymentTypeDesc(paymentEnum.getDesc());
}
orderVO.setOrderNo(order.getOrderNo());
orderVO.setCreateTime(DateUtils.dateToStr(order.getCreateTime()));
     return orderVO;

    }

    private ShippingVO assembleShippingVO(Shipping shipping){
        ShippingVO shippingVO=new ShippingVO();
        if(shipping!=null){
            shippingVO.setReceiverAddress(shipping.getReceiverAddress());
            shippingVO.setReceiverCity(shipping.getReceiverCity());
            shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
            shippingVO.setReceiverMobile(shipping.getReceiverMobile());
            shippingVO.setReceiverName(shipping.getReceiverName());
            shippingVO.setReceiverPhone(shipping.getReceiverPhone());
            shippingVO.setReceiverProvince(shipping.getReceiverProvince());
            shippingVO.setReceiverZip(shipping.getReceiverZip());

        }
        return shippingVO;
    }

    private OrderItemVO assembleOrderItemVO (OrderItem orderItem){
OrderItemVO orderItemVO=new OrderItemVO();
if(orderItem!=null){
    orderItemVO.setQuantity(orderItem.getQuantity());
    orderItemVO.setCreateTime(DateUtils.dateToStr(orderItem.getCreateTime()));
    orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
    orderItemVO.setOrderNo(orderItem.getOrderNo());
    orderItemVO.setProductId(orderItem.getProductId());
    orderItemVO.setProductImage(orderItem.getProductImage());
    orderItemVO.setProductName(orderItem.getProductName());
    orderItemVO.setTotalPrice(orderItem.getTotalPrice());
}

        return orderItemVO;
    }


/*
*清空购物车中已经选中的商品
 */
private  void  cleanCart(List<Cart> cartList){

if(cartList!=null&& cartList.size()>0){
    cartMapper.batchDelete(cartList);
}
}

/*
*扣库存
 */
private void reduceProductStock(List<OrderItem> orderItemList){

if(orderItemList!=null||orderItemList.size()>0){
    for(OrderItem orderItem:orderItemList){
      Integer  productId=  orderItem.getProductId();
      Integer quantity=orderItem.getQuantity();
      Product product=productMapper.selectByPrimaryKey(productId);
      product.setStock(product.getStock()-quantity);
      productMapper.updateByPrimaryKey(product);
    }
}
}

/*
*订单取消恢复库存
 */
private void addProductStock(Long orderNo){

    if(orderNo!=null){
           List<OrderItem>  orderItemList=orderItemMapper.selectByOrderNo(orderNo);
           for(OrderItem orderItem:orderItemList){
               Integer  productId=orderItem.getProductId();
               Integer quantity=orderItem.getQuantity();
               Product product=productMapper.selectByPrimaryKey(productId);
               product.setStock(product.getStock()+quantity);
               productMapper.updateByPrimaryKey(product);
           }
    }
}
    /*
     *计算订单总价格
     *
     */
    private  BigDecimal getOrderPrice(List<OrderItem> orderItemList){
      BigDecimal bigDecimal=new BigDecimal(0);
       for(OrderItem orderItem:orderItemList){
           BigDecimalUtils.add(bigDecimal.doubleValue(),orderItem.getTotalPrice().doubleValue());
       }
       return bigDecimal;
    }

/*
*创建订单
*
 */
private Order createOrder(Integer userId,Integer shippingId,BigDecimal orderTotalPrice){
    Order order=new Order();
    order.setOrderNo(generateOrderNO());
    order.setShippingId(shippingId);
    order.setUserId(userId);
   order.setStatus(Const.OrderStatusEnum.ORDER_UN_PAY.getCode());
//订单金额
    order.setPayment(orderTotalPrice);
    order.setPostage(0);
    order.setPaymentType(Const.PaymentEnum.ONLINE.getCode());


//保存订单
    int result=orderMapper.insert(order);
    if(result>0){
        return  order;
    }
    return null;
}

    /*
     *生成订单编号
     *
     */

    private Long generateOrderNO(){


        return System.currentTimeMillis()+new Random().nextInt(100);
    }

/*
*根据购物车中添加的订单商品查询订单的商品详情
 */
    private   ServerResponse  getCartOrderItem(Integer userId,List<Cart> cartList){

        if(cartList==null ||cartList.size()==0){
            return ServerResponse.serverResponseByError("购物车为空");
        }
            List<OrderItem> orderItemList=Lists.newArrayList();
            for(Cart cart:cartList){

                OrderItem orderItem=new OrderItem();
                orderItem.setUserId(userId);
                Product product=productMapper.selectByPrimaryKey(cart.getProductId());
                if(product==null){
                    return ServerResponse.serverResponseByError("id为"+cart.getProductId()+"的商品不存在");
                }
                if(product.getStatus()!=Const.ProductStatusEnum.PRODUCT_ONLINE.getCode()){
                    return ServerResponse.serverResponseByError("id为"+cart.getProductId()+"的商品已经下架");
                }
                if(product.getStock()<cart.getQuantity()){
                    return ServerResponse.serverResponseByError("id为"+cart.getProductId()+"的商品库存不足");
                }
                orderItem.setQuantity(cart.getQuantity());
                orderItem.setCurrentUnitPrice(product.getPrice());
                orderItem.setProductId(product.getId());
                orderItem.setProductImage(product.getMainImage());
                orderItem.setProductName(product.getName());
                orderItem.setTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));
                orderItemList.add(orderItem);
            }
        return ServerResponse.serverResponseBySuccess(orderItemList);
    }





}
