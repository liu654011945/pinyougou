package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.sellergoods.service
 */
public interface BrandService {

    public List<TbBrand> findAll();

    /**
     * 分页查询
     * @param pageNum  页码
     * @param pageSize 每页显示的行
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);

    /**
     * 添加一个品牌数据
     * @param brand
     */
    public void add(TbBrand brand);

    public TbBrand findOne(Long id);

    /**
     * 更新后的数据 而且一定要有ID的值
     * @param brand
     */
    public void update(TbBrand brand);

    /**
     *
     * @param ids 从前端传递过来要删除的品牌iD的集合
     */
    public void delete(Long[] ids);

    /**
     *
     * @param pageNum
     * @param pageSize
     * @param brand 分页查询时的搜索的条件
     * @return
     */
    public PageResult search(int pageNum, int pageSize,TbBrand brand);
}
