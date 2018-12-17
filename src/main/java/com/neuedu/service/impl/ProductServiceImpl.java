package com.neuedu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.Product;
import com.neuedu.service.ICategoryService;
import com.neuedu.service.IProductService;
import com.neuedu.utils.DateUtils;
import com.neuedu.utils.FTPUtil;
import com.neuedu.utils.PropertiesUtils;
import com.neuedu.vo.ProductDetailVO;
import com.neuedu.vo.ProductListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ProductServiceImpl implements IProductService {
@Autowired
ProductMapper productMapper;
@Autowired
CategoryMapper categoryMapper;
@Autowired
ICategoryService  categoryService;

    /*
     *后台产品接口
     */

    @Override
    public ServerResponse saveOrUpdate(Product product) {
       //step1:非空校验
          if(product==null){
              return ServerResponse.serverResponseByError("参数不能为空");
          }

        //step2:sub_images -->1.jpg,2.jpg,3.jpg
          String subImages=product.getSubImages();
          if(subImages!=null && !subImages.equals("")){
              String []subImgeArr =subImages.split(",");
              if(subImgeArr.length>0){
                  //设置商品的主图
                  product.setMainImage(subImgeArr[0]);
              }
          }
        //step3:save  or  update
if(product.getId()==null){
              //添加
int result=productMapper.insert(product);
if(result>0){
    return ServerResponse.serverResponseBySuccess("添加成功");
}else{
    return ServerResponse.serverResponseByError("添加失败");
}
}else{
              //更新
    int result=productMapper.updateByPrimaryKey(product);
    if(result>0){
        return ServerResponse.serverResponseBySuccess("更新成功");
    }else{
        return ServerResponse.serverResponseByError("更新失败");
    }
}

    }

    @Override
    public ServerResponse set_sale_status(Integer productId, Integer status) {

        //step1:参数非空校验
        if(productId==null){
            return ServerResponse.serverResponseByError("参数id不能为空");
        }
        if(status==null){
            return ServerResponse.serverResponseByError("参数status不能为空");
        }
        //step2: 更新商品状态
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
         int result=productMapper.updateProductKeySelecttive(product);

        //step3:返回结果

        if(result>0){
            return ServerResponse.serverResponseBySuccess("更新成功");
        }else{
            return ServerResponse.serverResponseByError("更新失败");
        }

    }

    @Override
    public ServerResponse detail(Integer productId) {
        //step1:参数校验
        if(productId==null){
            return ServerResponse.serverResponseByError("参数id不能为空");
        }

        //step2:查询product
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.serverResponseByError("商品不存在");
        }
        //step3:product-->productDetailVO
        ProductDetailVO productDetailVO=assembleProductDetailVO(product);
        //step4:返回结果
        return ServerResponse.serverResponseBySuccess(productDetailVO);
    }



    private ProductDetailVO assembleProductDetailVO(Product product){





        ProductDetailVO productDetailVO=new ProductDetailVO();
        productDetailVO.setCategoryId(product.getCategoryId());
    productDetailVO.setCreateTime(DateUtils.dateToStr(product.getCreateTime()));
    productDetailVO.setDetail(product.getDetail());
    productDetailVO.setImageHost(PropertiesUtils.readByKey("imageHost"));
    productDetailVO.setName(product.getName());
    productDetailVO.setId(product.getId());
    productDetailVO.setMainImage(product.getMainImage());
    productDetailVO.setPrice(product.getPrice());
    productDetailVO.setStatus(product.getStatus());
    productDetailVO.setStock(product.getStock());
    productDetailVO.setSubImages(product.getSubImages());
    productDetailVO.setSubtitle(product.getSubtitle());
    productDetailVO.setUpdateTime(DateUtils.dateToStr(product.getUpdateTime()));
Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
if(category!=null){
    productDetailVO.setCategoryId(category.getParentId());
}else {
    //默认为空节点
    productDetailVO.setParentCategoryId(0);
}
        return productDetailVO;

}

    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {
        //分页插件
        PageHelper.startPage(pageNum,pageSize);
        //step1:查询商品数据
        List<Product>productList = productMapper.selectAll();
        List<ProductListVO>productListVOList= Lists.newArrayList();
        if(productList!=null &&  productList.size()>0){
            for(Product product:productList){
                ProductListVO productListVO=assembleproductListVO(product);
                productListVOList.add(productListVO);
            }
        }
        PageInfo pageInfo=new PageInfo(productListVOList);


        return ServerResponse.serverResponseBySuccess(pageInfo);
    }



    private  ProductListVO assembleproductListVO(Product product){
ProductListVO productListVO=new ProductListVO();
productListVO.setId(product.getId());
productListVO.setCategoryId(product.getCategoryId());
productListVO.setMainImage(product.getMainImage());
productListVO.setName(product.getName());
productListVO.setPrice(product.getPrice());
productListVO.setSubtitle(product.getSubtitle());
productListVO.setStatus(product.getStatus());


return productListVO;
}
/*
*搜索商品
 */
    @Override
    public ServerResponse search(Integer productId, String productName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        if(productName!=null &&productName.equals("")){
            productName="%"+productName+"%";
        }
List<Product> productList=productMapper.findProductByProductIdAndProductName(productId,productName);
        List<ProductListVO>productListVOList= Lists.newArrayList();
        if(productList!=null &&  productList.size()>0){
            for(Product product:productList){
                ProductListVO productListVO=assembleproductListVO(product);
                productListVOList.add(productListVO);
            }
        }

         PageInfo pageInfo=new PageInfo(productListVOList);

        return ServerResponse.serverResponseBySuccess(pageInfo);
    }
    /*
     *更新商品信息
     */
    @Override
    public ServerResponse upload(MultipartFile file, String path) {

        //step1:参数非空
        if(file==null){
            return ServerResponse.serverResponseByError("file为空");
        }
        //:获取图片名称
       String originalFilename= file.getOriginalFilename();
        //获取图片的扩展名
       String exName= originalFilename.substring(originalFilename.lastIndexOf("."));//.jsp
        //生成 新的图片名称（唯一）
        String newFileNmae=UUID.randomUUID().toString()+exName;

        File pathFile=new File(path);
        if(!pathFile.exists()){
            pathFile.setWritable(true);
            pathFile.mkdir();
        }

        File file1=new File(path,newFileNmae);
        try {
            file.transferTo(file1);
            //图片上传到图片服务器

            FTPUtil.uploadFile(Lists.<File>newArrayList(file1));
            //.....
            Map<String,String> map= Maps.newHashMap();
            map.put("uri",newFileNmae);
            map.put("url",PropertiesUtils.readByKey("imageHost")+"/"+newFileNmae);

            //删除应用服务器上的图片
              file1.delete();

            return ServerResponse.serverResponseBySuccess(map);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return ServerResponse.serverResponseByError("图片上传失败");
    }
/*
*前台产品接口
 */
    @Override
    public ServerResponse detail_portal(Integer productId) {

        //step1:参数校验
        if(productId==null){
            return ServerResponse.serverResponseByError("参数id不能为空");
        }

        //step2:查询product
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.serverResponseByError("商品不存在");
        }
        //step3:检验商品状态
if(product.getStatus()!= Const.ProductStatusEnum.PRODUCT_ONLINE.getCode()){
    return ServerResponse.serverResponseByError("商品已下架");
}


        //step4:返回productDetailVO
ProductDetailVO productDetailVO=assembleProductDetailVO(product);

        //step5:返回结果


        return ServerResponse.serverResponseBySuccess(productDetailVO);
    }
/*
*产品列表
 */
    @Override
    public ServerResponse list_portal(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy) {

      //step1:参数校验  categoryId和keyword不能同时为空
if(categoryId==null&&(keyword==null||keyword.equals(""))){
    return ServerResponse.serverResponseByError("参数错误");
}
        //step2:catagoryId
        Set<Integer> integerSet= Sets.newHashSet();
      if(categoryId!=null){
    Category category = categoryMapper.selectByPrimaryKey(categoryId);
          if(category==null&&(keyword==null||keyword.equals(""))){
              //说明没有商品数据
              PageHelper .startPage(pageNum,pageSize);
              List<ProductListVO> productListVOList=Lists.newArrayList();
              PageInfo pageInfo=new PageInfo(productListVOList);
              return ServerResponse.serverResponseBySuccess(pageInfo);
          }
          ServerResponse serverResponse= categoryService.get_deep_category(categoryId);

          if(serverResponse.isSuccess()){
           integerSet=(Set<Integer>)serverResponse.getData();
          }

      }
//step3:keyword
        if(keyword!=null&&!keyword.equals("")){
            keyword="%"+keyword+"%";
        }

        if(orderBy.equals("")){
            PageHelper.startPage(pageNum,pageSize);
        }else {
           String[] orderByArr= orderBy.split("_");
           if(orderByArr.length>1){
               PageHelper.startPage(pageNum,pageSize,orderByArr[0]+" "+orderByArr[1]);
           }else{
               PageHelper.startPage(pageNum,pageSize);
           }
        }
        //step4:List<Product> -->List<ProductListVO>
        List<Product> productList=productMapper.searchProduct(integerSet,keyword);
        List<ProductListVO>productListVOList=Lists.newArrayList();
        if(productList!=null && productList.size()>0){
            for(Product product:productList){
             ProductListVO productListVO=assembleproductListVO(product);
                productListVOList.add(productListVO);
            }
        }
        //step5:分页
  PageInfo pageInfo=new PageInfo();
        pageInfo.setList(productListVOList);
        //step6:返回


        return ServerResponse.serverResponseBySuccess(pageInfo);
    }


}
