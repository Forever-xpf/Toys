package com.neuedu.controller.backend;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.awt.SunHints;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/manage/category")
public class CategoryManageController {
    @Autowired
    ICategoryService categoryService;
    /*
*获取种类子节点(平级)
 */
@RequestMapping(value = "/get_category.do")
public ServerResponse get_category(HttpSession session,Integer categoryId){

    return categoryService.get_category(categoryId);
}

    /*
     *增加节点
     */
    @RequestMapping(value = "/add_category.do")
    public ServerResponse add_category(HttpSession session,@RequestParam(required = false,defaultValue = "0") Integer parentId,String categoryName ){

        return categoryService.add_category(parentId,categoryName);
    }
    /*
     *修改节点
     */
    @RequestMapping(value = "/set_category_name.do")
    public ServerResponse set_category_name(HttpSession session, Integer categoryId,String categoryName ){

        return categoryService.set_category_name(categoryId,categoryName);
    }
    /*
     *获取当前分类id及递归子节点categoryId
     */
    @RequestMapping(value = "/get_deep_category.do")
    public ServerResponse get_deep_category(HttpSession session, Integer categoryId){

        return categoryService.get_deep_category(categoryId);
    }


}
