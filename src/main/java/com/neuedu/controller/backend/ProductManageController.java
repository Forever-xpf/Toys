package com.neuedu.controller.backend;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Product;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IProductService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value="/manage/product")
public class ProductManageController {
    @Autowired
IProductService productService;


    /*
    *新增or更新商品
     */
    @RequestMapping(value="/save.do")
    public ServerResponse saveOrUpdate(HttpSession session, Product product){

        return productService.saveOrUpdate(product);
    }


    /*
     *产品的上下架
     */
    @RequestMapping(value="/set_sale_status.do")
    public ServerResponse set_sale_status(HttpSession session,Integer productId,Integer status){

        return productService.set_sale_status(productId,status);
    }

    /*
     *产品详情
     */
    @RequestMapping(value="/detail.do")
    public ServerResponse detail(HttpSession session,Integer productId){

        return productService.detail(productId);
    }
    /*
     *产品列表
     */
    @RequestMapping(value="/list.do")
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum" ,required = false,defaultValue = "1")Integer pageNum,
                                                    @RequestParam(value = "pageSize" ,required = false,defaultValue = "10")Integer pageSize){


        return productService.list(pageNum,pageSize);
    }

    /*
     *产品搜索
     */
    @RequestMapping(value="/search.do")
    public ServerResponse search(HttpSession session,@RequestParam(value = "productId" ,required = false)Integer productId,
                                 @RequestParam(value = "productName" ,required = false)String productName,
                                 @RequestParam(value = "pageNum" ,required = false,defaultValue = "1")Integer pageNum,
                               @RequestParam(value = "pageSize" ,required = false,defaultValue = "10")Integer pageSize){

        return productService.search(productId,productName,pageNum,pageSize);
    }


}
