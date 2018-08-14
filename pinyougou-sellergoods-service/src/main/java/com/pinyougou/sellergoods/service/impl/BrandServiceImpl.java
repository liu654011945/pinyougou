package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.sellergoods.service.impl
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;
    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageResult result = new PageResult();
        //1.用到分页的插件 pagehelper
        PageHelper.startPage(pageNum,pageSize);
        //2.紧跟着的第一个查询才会被分页
        List<TbBrand> brandList = brandMapper.selectByExample(null);
        System.out.println("size"+brandList.size());
        List<TbBrand> brandList2 = brandMapper.selectByExample(null);
        System.out.println("size2:"+brandList2.size());
        Page<TbBrand> page = (Page<TbBrand>)brandList;
        result.setRows(page.getResult());//每页的集合
        result.setTotal(page.getTotal());//总记录数
        return result;
    }

    @Override
    public void add(TbBrand brand) {
        brandMapper.insertSelective(brand);
    }

    @Override
    public TbBrand findOne(Long id) {
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(id);
        return tbBrand;
    }

    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Long[] ids) {
//        for (Long id : ids) {
//            brandMapper.deleteByPrimaryKey(id);
//        }
        TbBrandExample exmaple = new TbBrandExample();//条件
        TbBrandExample.Criteria criteria = exmaple.createCriteria();
        criteria.andIdIn(Arrays.asList(ids));
        brandMapper.deleteByExample(exmaple);//delete from brand where id in (1,2,3)
    }

    @Override
    public PageResult search(int pageNum, int pageSize, TbBrand brand) {
        PageResult pageResult = new PageResult();
        //1.设置查询的条件
        TbBrandExample example = new TbBrandExample();


       if(brand!=null){
           TbBrandExample.Criteria criteria = example.createCriteria();
           //表示名称不为空
           if (brand.getName() != null && brand.getName().length()>0) {
               criteria.andNameLike("%"+brand.getName()+"%");//name like '%张三%'
           }
           if (brand.getFirstChar() != null && brand.getFirstChar().length()>0) {
               criteria.andFirstCharEqualTo(brand.getFirstChar());
           }
       }
        PageHelper.startPage(pageNum,pageSize);
        //2.执行查询
        Page<TbBrand> brandList = (Page<TbBrand>) brandMapper.selectByExample(example);

        //3.设置分页对象
        pageResult.setTotal(brandList.getTotal());
        pageResult.setRows(brandList.getResult());
        return pageResult;
    }
}
