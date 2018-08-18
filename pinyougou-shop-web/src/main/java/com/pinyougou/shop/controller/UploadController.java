package com.pinyougou.shop.controller;

import com.pinyougou.common.util.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.shop.controller
 */
@RestController
public class UploadController {
    @Value("${IMAGE_SERVER_URL}")
    private String IMAGE_SERVER_URL;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fastdfs_client.conf");
            //1.字节数组
            byte[] bytes = file.getBytes();
            //2.文件的扩展名
            String originalFilename = file.getOriginalFilename();
            //不带点
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
            String path = fastDFSClient.uploadFile(bytes,extName);//     group1/M00/00/04/wKgZhVt3npeANe0yAACc_YcOkik740.jpg
            String realpath = IMAGE_SERVER_URL+path;
            return new Result(true,realpath);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
