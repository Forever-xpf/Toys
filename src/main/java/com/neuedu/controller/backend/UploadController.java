package com.neuedu.controller.backend;

import com.neuedu.common.ServerResponse;
import com.neuedu.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/manage/product")
public class UploadController {
    @Autowired
    IProductService productService;
    @RequestMapping(value = "/upload",method = RequestMethod.GET)
    public String upload(){
        return "upload";  //逻辑视图    前缀（/WEB-INF/jsp/）+逻辑视图(upload)+后缀 (.jsp)  /WEB-INF/jsp/upload.jsp
    }

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    @ResponseBody
   public ServerResponse upload2(@RequestParam(value = "upload_file",required = false)MultipartFile file){


        String path="G:\\ftpfile";
        return productService.upload(file,path);  //逻辑视图    前缀（/WEB-INF/jsp/）+逻辑视图(upload)+后缀 (.jsp)  /WEB-INF/jsp/upload.jsp
    }





}
