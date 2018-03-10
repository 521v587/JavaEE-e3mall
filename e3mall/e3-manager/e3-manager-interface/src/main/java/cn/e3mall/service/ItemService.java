package cn.e3mall.service;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;

public interface ItemService {
	TbItem getItemById(long itemId);
	//查询所有商品列表
	EasyUIDataGridResult getItemList(int page, int rows);
	//添加商品
	E3Result addItem(TbItem item, String desc);
	//删除商品
	E3Result deleteItem(long[] ids);
	//上架
	E3Result upStatus(long[] ids);
	//下架
	E3Result downStatus(long[] ids);
	//根据item的id查询desc
	E3Result getItemDesc(long id);
	//根据item的id查询商品规格
	E3Result getItemParam(long id);
	//编辑商品
	
	TbItemDesc getItemDescById(long itemId);
}
