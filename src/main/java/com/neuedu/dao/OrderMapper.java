package com.neuedu.dao;

import com.neuedu.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_order
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_order
     *
     * @mbg.generated
     */
    int insert(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_order
     *
     * @mbg.generated
     */
    Order selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_order
     *
     * @mbg.generated
     */
    List<Order> selectAll();

    /**
     *后台修改商品状态为“已发货”
     */
    int updateByPrimaryKey(Order order);

    /*
    *根据userId和orderNo查询订单信息
     */
   Order findOrderByUserIdAndOrderId(@Param("userId") Integer userId,@Param("orderNo") Long orderNo);
    /*
     *根据userId和orderNo查询订单信息
     */
    Order findOrderByOrderNo(Long orderNo);
    /*
     *根据userId查询订单信息
     */
   List<Order> findOrderByUserId(Integer userId);
}