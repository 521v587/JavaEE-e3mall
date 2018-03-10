package cn.e3mall.search.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.search.mapper.ItemMapper;
import cn.e3mall.search.service.SearchItemService;
/**
 * 商品导入到索引库
 * @author fudin
 *
 */
@Service
public class SearchItemServiceImpl implements SearchItemService {
	@Autowired
	private SolrServer solrServer;
	@Autowired
	private ItemMapper itemMapper;

	@Override
	public E3Result importAllItems() {
		try{
			//查询所有的商品获得SearchItem集合
			List<SearchItem> itemList = itemMapper.getItemList();
			//创建一个SolrServer对象
			//创建一个SolrInputDocument对象
			for(SearchItem searchItem : itemList){
				SolrInputDocument document = new SolrInputDocument();
				document.addField("id", searchItem.getId());
				document.addField("item_title", searchItem.getTitle());
				document.addField("item_sell_point", searchItem.getSell_point());
				document.addField("item_price", searchItem.getPrice());
				document.addField("item_image", searchItem.getImage());
				document.addField("item_category_name", searchItem.getCategory_name());
				//将集合写入到Document对象
				solrServer.add(document);
			}
			//提交
			solrServer.commit();
			return E3Result.ok();
		}catch(Exception ex){
			ex.printStackTrace();
			return E3Result.build(500, "导入商品失败！");
		}
	}

}
