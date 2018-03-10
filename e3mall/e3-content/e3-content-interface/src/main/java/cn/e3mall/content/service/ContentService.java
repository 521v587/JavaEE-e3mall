package cn.e3mall.content.service;

import java.util.List;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbContent;

public interface ContentService {
	//查询tb_content数据表
	EasyUIDataGridResult getContentList(int page, int rows);
	//添加内容
	E3Result addContent(TbContent content);
	//根据cid查询内容列表
	List<TbContent> getContentListByCid(long cid);
}
