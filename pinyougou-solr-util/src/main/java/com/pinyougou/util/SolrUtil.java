package com.pinyougou.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.util
 * @since 1.0
 */
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void importAllItemData(){
        //1.从数据库查询数据
        TbItemExample exmaple = new TbItemExample();
        exmaple.createCriteria().andStatusEqualTo("1");//状态是正常的商品
        List<TbItem> tbItems = itemMapper.selectByExample(exmaple);
        //2.使用solrtemplate 数据导入

        //设置规格对应的动态域
        for (TbItem tbItem : tbItems) {
            String spec = tbItem.getSpec();
            Map map = JSON.parseObject(spec, Map.class);
            tbItem.setSpecMap(map);
        }
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();

    }

    public static void main(String[] args) {
        //先初始化spring容器
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        //调用方法
        SolrUtil bean = context.getBean(SolrUtil.class);
        bean.importAllItemData();
    }
}
