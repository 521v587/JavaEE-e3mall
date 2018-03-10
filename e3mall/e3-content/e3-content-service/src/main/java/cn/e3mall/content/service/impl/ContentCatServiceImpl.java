package cn.e3mall.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.alibaba.dubbo.rpc.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCatService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;

@Service
public class ContentCatServiceImpl implements ContentCatService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	/**
	 * 商品内容分类管理
	 */
	@Override
	public List<EasyUITreeNode> getContentCatList(long parentId) {
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> contentCatList = contentCategoryMapper.selectByExample(example);
		List<EasyUITreeNode> nodeList = new ArrayList<>();
		//转换成node集合
		for(TbContentCategory contentCatrgory : contentCatList){
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(contentCatrgory.getId());
			node.setState(contentCatrgory.getIsParent()? "closed":"open");
			node.setText(contentCatrgory.getName());
			//添加节点到集合中
			nodeList.add(node);
		}
		return nodeList;
		
	}
	@Override
	public E3Result addContentCategory(long parentId, String name) {
		//创建一个TbContentCategory对象
		TbContentCategory contentCategory = new TbContentCategory();
		//补全数据
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//新添加的结点一定是叶子结点
		contentCategory.setIsParent(false);
		//默认排序就是1
		contentCategory.setSortOrder(1);
		//状态。可选值:1(正常),2(删除)
		contentCategory.setStatus(1);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		//插入到数据库中
		contentCategoryMapper.insert(contentCategory);
		//判断父结点isParent属性，如果不是true改为true
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parent.getIsParent()){
			parent.setIsParent(true);
			//更新到数据库中
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		//将该对象封装到E3Result中,含有id对象
		return E3Result.ok(contentCategory);
	}
	/**
	 * 更新结点
	 */
	@Override
	public E3Result updateContentCategory(long id, String name) {
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		contentCategory.setName(name);
		contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
		return E3Result.ok(contentCategory);
	}
	/**
	 * 删除结点
	 */
	@Override
	public void deleteContentCategory(long id) {
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		//如果不是父结点，可以删除
		if(!contentCategory.getIsParent()){
			contentCategoryMapper.deleteByPrimaryKey(id);
			Long parentId = contentCategory.getParentId();
			//判断父节点还有没有叶子结点，如果没有，将isParent改为0
			//该类目是否为父类目，1为true，0为false
			TbContentCategoryExample example = new TbContentCategoryExample();
			Criteria criteria = example.createCriteria();
			//设置条件来查找父节点
			criteria.andParentIdEqualTo(parentId);
			//************************************************
			int childrenNumber = contentCategoryMapper.countByExample(example);
			//判断父结点还有没有子结点
			if(childrenNumber == 0) {
				//获得该父结点
				TbContentCategory parentNode = contentCategoryMapper.selectByPrimaryKey(parentId);
				parentNode.setIsParent(false);
				//将更新后的结点更新到数据库中
				contentCategoryMapper.updateByPrimaryKey(parentNode);
			}
		}
	}
}
