package cn.e3mall.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbContentExample.Criteria;
/**
 * 内容管理
 * @author fudin
 *
 */
@Service
public class ContentServiceImpl implements ContentService {
	@Autowired
	private TbContentMapper contentMapper;
	
	@Autowired
	private JedisClient jedisClient;
	//内容列表再redis缓存中的key
	@Value(value = "CONTENT_LIST")
	private String CONTENT_LIST;

	/**
	 * 获得contentList
	 */
	@Override
	public EasyUIDataGridResult getContentList(int page, int rows) {
		//设置查询信息
		PageHelper.startPage(page, rows);
		//查询所有的结果
		TbContentExample example = new TbContentExample();
		List<TbContent> contentList = contentMapper.selectByExample(example);
		//返回的结果集对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		//设置结果集对象参数
		result.setRows(contentList);
		//分页结果
		PageInfo<TbContent> pageInfo = new PageInfo<>(contentList);
		//取分页结果总记录数
		long total = pageInfo.getTotal();
		//设置结果集对象参数
		result.setTotal(total);
		return result;
	}

	@Override
	public E3Result addContent(TbContent content) {
		//补全日期参数
		content.setCreated(new Date());
		content.setUpdated(new Date());
		//向数据库中插入数据
		contentMapper.insert(content);
		//同步缓存，删除缓存中对应的数据
		jedisClient.hdel(CONTENT_LIST, content.getCategoryId().toString());
		return E3Result.ok();
	}

	/**
	 * 根据cid查询ContentList
	 * 使用redis做缓存
	 */
	@Override
	public List<TbContent> getContentListByCid(long cid) {
		//判断缓存中有没有数据
		String json = jedisClient.hget(CONTENT_LIST, cid + "");
		//准备返回的数据
		List<TbContent> contentList = null;
		if(StringUtils.isNotBlank(json)){
			//有的话从缓存中取出
			//缓存需要同步
			try{
				contentList = JsonUtils.jsonToList(json, TbContent.class);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}else{
			//没有的话查询数据库，并保存到缓存中
			TbContentExample example = new TbContentExample();
			//设置查询条件
			Criteria criteria = example.createCriteria();
			criteria.andCategoryIdEqualTo(cid);
			//查询contentList
			contentList = contentMapper.selectByExampleWithBLOBs(example);
			//保存contentList到缓存中
			try{
				jedisClient.hset(CONTENT_LIST, cid + "", JsonUtils.objectToJson(contentList));
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return contentList;
	}

}
