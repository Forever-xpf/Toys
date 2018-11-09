package com.neuedu.service.impl;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Product;

import javax.servlet.http.HttpSession;

public interface IProductService {

 ServerResponse saveOrUpdate(Product product);


}
