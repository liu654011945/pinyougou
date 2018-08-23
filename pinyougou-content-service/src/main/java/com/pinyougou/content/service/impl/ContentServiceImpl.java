package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {


		// key:PINYOUGOU_CONTENT_KEY  field(categoryId) value(List<tbcontent>)
		//key:PINYOUGOU_CONTENT_KEY  field(categoryId) value(List<tbcontent>)
		contentMapper.insert(content);

		//清空缓存
		try {
			redisTemplate.boundHashOps("PINYOUGOU_CONTENT_KEY").delete(content.getCategoryId());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){

		//1.清空原来的广告对应的分类 的缓存

		Long categoryId = content.getCategoryId();//更新后的分类的ID

		Long categoryId1 = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();//原来的分类的ID

		try {
			redisTemplate.boundHashOps("PINYOUGOU_CONTENT_KEY").delete(categoryId);
			redisTemplate.boundHashOps("PINYOUGOU_CONTENT_KEY").delete(categoryId1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//2.清空更新之后的广告对应分类的缓存

		contentMapper.updateByPrimaryKey(content);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbContent tbContent = contentMapper.selectByPrimaryKey(id);
			redisTemplate.boundHashOps("PINYOUGOU_CONTENT_KEY").delete(tbContent.getCategoryId());
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getContent()!=null && content.getContent().length()>0){
				criteria.andContentLike("%"+content.getContent()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;

    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {


		//1.先从缓存中获取数据   判断 如果有数据  直接返回
		try {
			List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("PINYOUGOU_CONTENT_KEY").get(categoryId);

			if (contentList != null && contentList.size()>0) {
				System.out.println("有缓存");
				return contentList;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

		TbContentExample exmaple = new TbContentExample();
		exmaple.createCriteria().andCategoryIdEqualTo(categoryId);
		List<TbContent> contents = contentMapper.selectByExample(exmaple);

		//2.将数据存入到redis中

		try {
			redisTemplate.boundHashOps("PINYOUGOU_CONTENT_KEY").put(categoryId,contents);
			System.out.println("没有缓存");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// key:PINYOUGOU_CONTENT_KEY  field(categoryId) value(List<tbcontent>)
		//key:PINYOUGOU_CONTENT_KEY  field(categoryId) value(List<tbcontent>)


		return contents;
    }

}
