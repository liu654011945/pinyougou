package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 三国的包子 ThinkPad pinyougou-parent
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.portal.controller
 */
@RestController//@controller +@responsebody
@RequestMapping("/content")
public class IndexController {
    @Reference
    private ContentService contentService;

    @RequestMapping("/findByCategoryId")
    public List<TbContent> findByCategoryId(Long categoryId){
        return contentService.findContentListByCategoryId(categoryId);
    }

    

}
