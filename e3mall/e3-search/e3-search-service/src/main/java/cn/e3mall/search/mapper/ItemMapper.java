package cn.e3mall.search.mapper;

import java.util.List;

import cn.e3mall.common.pojo.SearchItem;

public interface ItemMapper {

	//获得查询商品的列表
	List<SearchItem> getItemList();
	SearchItem getItemById(long id);
}
