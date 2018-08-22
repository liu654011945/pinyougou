package com.pinyougou.sellergoods.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        TbGoods tbGoods = goods.getGoods();
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        //1.添加商品（SPU）的数据
        tbGoods.setAuditStatus("0");//默认是没有被审核的
        tbGoods.setIsDelete(false);//不删除
        goodsMapper.insert(tbGoods);//要主键返回
        //2.添加商品描述 的数据
        goodsDesc.setGoodsId(tbGoods.getId());//设置主键
        goodsDescMapper.insert(goodsDesc);
        //3.添加商品的列表(SKU列表)数据
        //TODO
        List<TbItem> itemList = goods.getItemList();


        //如果是启用规格选项
        if ("1".equals(tbGoods.getIsEnableSpec())) {
            for (TbItem tbItem : itemList) {
                //属性的补全
                //SKU商品的标题
                String title = tbGoods.getGoodsName();//
                String spec = tbItem.getSpec();//
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                for (String key : map.keySet()) {
                    title += " " + map.get(key);
                }
                tbItem.setTitle(title);

                //获取图片
                String itemImages = goodsDesc.getItemImages();

                List<Map> mapList = JSON.parseArray(itemImages, Map.class);

                if (mapList != null && mapList.size() > 0) {
                    String url = (String) mapList.get(0).get("url");
                    tbItem.setImage(url);
                }

                //
                tbItem.setCategoryid(tbGoods.getCategory3Id());

                TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());

                tbItem.setCategory(tbItemCat.getName());//分类名称

                tbItem.setCreateTime(new Date());
                tbItem.setUpdateTime(tbItem.getCreateTime());

                tbItem.setGoodsId(tbGoods.getId());

                //存放商家
                tbItem.setSellerId(tbGoods.getSellerId());
                TbSeller seller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
                tbItem.setSeller(seller.getNickName());//店铺名称
                //存放品牌名称

                TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
                tbItem.setBrand(tbBrand.getName());
                itemMapper.insert(tbItem);
            }
        } else {
            TbItem item = new TbItem();

            item.setTitle(tbGoods.getGoodsName());
            item.setPrice(tbGoods.getPrice());

            item.setNum(999);//默认值

            item.setStatus("1");
            item.setIsDefault("1");

            item.setSpec("{}");


            //获取图片
            String itemImages = goodsDesc.getItemImages();

            List<Map> mapList = JSON.parseArray(itemImages, Map.class);

            if (mapList != null && mapList.size() > 0) {
                String url = (String) mapList.get(0).get("url");
                item.setImage(url);
            }


            item.setCategoryid(tbGoods.getCategory3Id());

            TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());

            item.setCategory(tbItemCat.getName());//分类名称

            item.setCreateTime(new Date());
            item.setUpdateTime(item.getCreateTime());

            item.setGoodsId(tbGoods.getId());

            //存放商家
            item.setSellerId(tbGoods.getSellerId());
            TbSeller seller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
            item.setSeller(seller.getNickName());//店铺名称
            //存放品牌名称

            TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
            item.setBrand(tbBrand.getName());
            itemMapper.insert(item);
        }


    }


    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        TbGoods goods1 = goods.getGoods();
        goods1.setAuditStatus("0");
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();

        //1.更新商品SPU表
        goodsMapper.updateByPrimaryKey(goods1);
        //2.更新商品的描述

        goodsDescMapper.updateByPrimaryKey(goodsDesc);

        //3.更新商品的SKU列表
        List<TbItem> itemList = goods.getItemList();

        // 先删除原有的SKU列表
        TbItemExample exmpale = new TbItemExample();
        exmpale.createCriteria().andGoodsIdEqualTo(goods1.getId());
        itemMapper.deleteByExample(exmpale);//delete from tbitem where goodsid=1
        //再添加现在传递过来的SKU列表（更新后的数据）
        for (TbItem tbItem : itemList) {
            //属性的补全
            //SKU商品的标题
            String title = goods1.getGoodsName();//
            String spec = tbItem.getSpec();//
            Map<String, String> map = JSON.parseObject(spec, Map.class);
            for (String key : map.keySet()) {
                title += " " + map.get(key);
            }
            tbItem.setTitle(title);

            //获取图片
            String itemImages = goodsDesc.getItemImages();

            List<Map> mapList = JSON.parseArray(itemImages, Map.class);

            if (mapList != null && mapList.size() > 0) {
                String url = (String) mapList.get(0).get("url");
                tbItem.setImage(url);
            }

            //
            tbItem.setCategoryid(goods1.getCategory3Id());

            TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods1.getCategory3Id());

            tbItem.setCategory(tbItemCat.getName());//分类名称

            tbItem.setCreateTime(new Date());
            tbItem.setUpdateTime(tbItem.getCreateTime());

            tbItem.setGoodsId(goods1.getId());

            //存放商家
            tbItem.setSellerId(goods1.getSellerId());
            TbSeller seller = sellerMapper.selectByPrimaryKey(goods1.getSellerId());
            tbItem.setSeller(seller.getNickName());//店铺名称
            //存放品牌名称

            TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods1.getBrandId());
            tbItem.setBrand(tbBrand.getName());
            itemMapper.insert(tbItem);
        }













    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();

        //查询SPU的数据

        TbGoods goods1 = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(goods1);
        //查询描述的数据
        TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(goodsDesc);
        //查询SKU列表
        //select * from tbitem wehre goodsid=1
        TbItemExample exmaple = new TbItemExample();
        exmaple.createCriteria().andGoodsIdEqualTo(id);

        List<TbItem> tbItems = itemMapper.selectByExample(exmaple);

        goods.setItemList(tbItems);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
//            goodsMapper.deleteByPrimaryKey(id);
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setIsDelete(true);//要删除
            //逻辑删除
            goodsMapper.updateByPrimaryKey(goods);//update set is_delete=1 where id in(1,2,3);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
            //过滤掉删除的
        criteria.andIsDeleteEqualTo(false);//查询没有被删除的商品SPU列表
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(goods);
        }
    }

}
