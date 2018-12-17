package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Product;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

public interface IProductService {
    /*
     *新增or更新商品
     */

    ServerResponse saveOrUpdate(Product product);
    /*
     *产品的上下架接口
     * @param productId
     * @param  status
     */

    ServerResponse set_sale_status(Integer productId, Integer status);

    /*
     *后台—产品详情
     */
    ServerResponse detail(Integer productId);

    /*
     *后台—产品列表  分页
     */
    ServerResponse list(Integer pageNum, Integer pageSize);

    /*
     *后台—产品搜索
     */
    ServerResponse search(Integer productId, String productName, Integer pageNum, Integer pageSize);

    /*
     *图片上传
     */
    ServerResponse upload(MultipartFile file, String path);

    /*
     *前台—产品详情
     */
    ServerResponse detail_portal(Integer productId);

    /*
     *前台—获取商品
     */
    ServerResponse list_portal(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy);

}
