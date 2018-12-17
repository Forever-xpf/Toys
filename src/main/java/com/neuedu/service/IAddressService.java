package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Shipping;

public interface IAddressService {

    /*
    *添加收货地址
     */
    public ServerResponse add(Integer userId, Shipping shipping);
    /*
     *删除收货地址
     */
    public ServerResponse del(Integer userId, Integer shippingId);

    /*
     *更新收货地址
     */
    ServerResponse update(Shipping shipping);

    /*
     *查看收货地址
     */
   ServerResponse select(Integer shippingId);
    /*
     *查看收货地址列表
     */

   ServerResponse list(Integer pageNum,Integer pageSize);
}
