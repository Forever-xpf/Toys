package com.neuedu.service;

import com.neuedu.common.ServerResponse;

public interface ICartService {

    /*
    *购物车添加商品接口
     */
    public ServerResponse add(Integer userId,Integer productId, Integer count);


    /*
     *购物车添加商品接口
     */

     ServerResponse list(Integer userId);

    /*
     *购物车更新某个商品数量
     */
   ServerResponse update(Integer userId,Integer productId,Integer count);

    /*
     *移除购物车某个商品
     */
    ServerResponse delete_product(Integer userId,String productIds);

    /*
     *选中购物车某个商品
     */
   ServerResponse select(Integer userId,Integer productId,Integer check);
    /*
     *全选中购物车商品
     */
  ServerResponse  get_cart_product_count(Integer userId);
}
