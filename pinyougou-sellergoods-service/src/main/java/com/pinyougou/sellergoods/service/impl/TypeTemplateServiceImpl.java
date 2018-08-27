package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
	private TbSpecificationOptionMapper optionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}


	@Autowired
	private RedisTemplate redisTemplate;
	
		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		//紧跟着的第一个查询才会分页
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);

			//模板的数据更新时需要存储到reids中

			List<TbTypeTemplate> all = findAll();//查询所有的模板数据
			for (TbTypeTemplate tbTypeTemplate : all) {
				List<Map> mapList = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
				//存储品牌列表
				redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(),mapList);
				List<Map> specList = findSpecList(tbTypeTemplate.getId());////[{"id":27,"text":"网络",options:[{},{}]},{"id":32,"text":"机身内存"}]
				//存储规格列表
				redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(),specList);

			}




		return new PageResult(page.getTotal(), page.getResult());
	}

	//[{"id":27,"text":"网络",options:[{},{}]},{"id":32,"text":"机身内存"}]
    @Override
    public List<Map> findSpecList(Long typeTemplateId) {
		TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(typeTemplateId);
		String specIds = tbTypeTemplate.getSpecIds();//这是规格的数据 [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
		List<Map> mapList = JSON.parseArray(specIds, Map.class);//

		for (Map map : mapList) {//map:{"id":27,"text":"网络"}
			Long specId = Long.valueOf((Integer)map.get("id"));
			TbSpecificationOptionExample exmaple = new TbSpecificationOptionExample();
			exmaple.createCriteria().andSpecIdEqualTo(specId);
			List<TbSpecificationOption> options = optionMapper.selectByExample(exmaple);//select * from option where specid=27
			map.put("options",options);
		}
		return mapList;
    }

}
