package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.search.service.impl
 * @since 1.0
 */
@Service(timeout = 1000000)//默认dubbo的超时时间是1S 钟  里面的单位是：毫秒
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map search(Map searchMap) {
        Map resultMap = new HashMap();
        //高亮
        Map map = searchList(searchMap);

        //分组
        Map map1 = searchCategoryList(searchMap);

        resultMap.putAll(map1);
        resultMap.putAll(map);

        //规格和品牌列表查询 如果不点击 默认使用第一个分类名称
        List<String> categoryList = (List<String>) map1.get("categoryList");
        String category = (String) searchMap.get("category");
        if (category != null && category.length()>0) {
            Map map2 = searchBrandAndSpecList(category);
            resultMap.putAll(map2);
        }else{
            Map map2 = searchBrandAndSpecList(categoryList.get(0));
            resultMap.putAll(map2);
        }
        return resultMap;
    }


    //分组查询
    private Map searchCategoryList(Map searchMap){
        Map map = new HashMap();

        List<String> categoryList=new ArrayList<>();

        String keywords = (String) searchMap.get("keywords");//三星
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords");
        criteria.is(keywords);//item_keywords:三星
        query.addCriteria(criteria);

        //3.设置分组选项

        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");//分类来分组
        query.setGroupOptions(groupOptions);
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> item_category = groupPage.getGroupResult("item_category");//分组的结果
        Page<GroupEntry<TbItem>> groupEntries = item_category.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            System.out.println(tbItemGroupEntry.getGroupValue());
            categoryList.add(tbItemGroupEntry.getGroupValue());
        }

        map.put("categoryList", categoryList);//当前页的结果  LIst<String>=["平板电视","手机"]
        return map;
    }


    //高亮显示的查询
    private Map searchList(Map searchMap) {
        Map map = new HashMap();
        //1.先获取参数的值
        String keywords = (String) searchMap.get("keywords");//三星

        //2.创建一个query对象 设置 条件
//        Query query = new SimpleQuery("*:*");

        HighlightQuery query = new SimpleHighlightQuery();
        Criteria criteria = new Criteria("item_keywords");
        criteria.is(keywords);//item_keywords:三星
        query.addCriteria(criteria);

        //3.设置高亮选项

        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");//设置高亮显示的域
        highlightOptions.setSimplePrefix("<em style=\"color:red\">");
        highlightOptions.setSimplePostfix("</em>");


        query.setHighlightOptions(highlightOptions);


        //过滤查询

        //商品分类过滤查询
        String category = (String) searchMap.get("category");
        if (category != null && category.length()>0) {
            Criteria criteria1 = new Criteria("item_category");
            criteria1.is(category);//item_category:手机
            FilterQuery fileterquery = new SimpleFilterQuery(criteria1);
            query.addFilterQuery(fileterquery);
        }

        //品牌过滤

        String brand = (String) searchMap.get("brand");
        if (brand != null && brand.length()>0) {
            Criteria criteria2 = new Criteria("item_brand");
            criteria2.is(brand);//item_category:手机
            FilterQuery fileterquery = new SimpleFilterQuery(criteria2);
            query.addFilterQuery(fileterquery);
        }

        //过滤规格
        Map<String,String> spec = (Map<String, String>) searchMap.get("spec");

        if (spec != null) {
            for (String key : spec.keySet()) {
                Criteria criteria2 = new Criteria("item_spec_"+key);
                criteria2.is(spec.get(key));//item_spec_网络:3G
                FilterQuery fileterquery = new SimpleFilterQuery(criteria2);
                query.addFilterQuery(fileterquery);
            }
        }



        //4.执行查询
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //获取高亮
        List<HighlightEntry<TbItem>> highlighted = highlightPage.getHighlighted();
        for (HighlightEntry<TbItem> tbItemHighlightEntry : highlighted) {
            TbItem entity = tbItemHighlightEntry.getEntity();//对象

           /* List<HighlightEntry.Highlight> highlights = tbItemHighlightEntry.getHighlights();
            for (HighlightEntry.Highlight highlight : highlights) {
                List<String> snipplets = highlight.getSnipplets();
                for (String snipplet : snipplets) {
                    entity.setTitle(snipplet);
                }
            }*/
            if (tbItemHighlightEntry.getHighlights() != null
                    && tbItemHighlightEntry.getHighlights().size() > 0
                    && tbItemHighlightEntry.getHighlights().get(0) != null
                    && tbItemHighlightEntry.getHighlights().get(0).getSnipplets() != null
                    && tbItemHighlightEntry.getHighlights().get(0).getSnipplets().size() > 0) {
                entity.setTitle(tbItemHighlightEntry.getHighlights().get(0).getSnipplets().get(0));
            }


        }


        List<TbItem> content = highlightPage.getContent();//当前页的结果集合
        map.put("total",highlightPage.getTotalElements());
        System.out.println(">>总记录数："+highlightPage.getTotalElements());
        map.put("rows", content);//当前页的结果
        return map;
    }

    @Autowired
    private RedisTemplate redisTemplate;
    //根据分类名称查询规格和品牌列表
    public Map searchBrandAndSpecList(String categoryName){
        Map map = new HashMap();

        //1.使用redis根据分类名查询模板的ID
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(categoryName);
        //2.使用redis根据模板的ID 查询品牌列表

        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);

        //3.使用redis 根据模板的ID 查询规格的列表
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);


        map.put("brandList",brandList);
        map.put("specList",specList);
        return map;
    }

}
