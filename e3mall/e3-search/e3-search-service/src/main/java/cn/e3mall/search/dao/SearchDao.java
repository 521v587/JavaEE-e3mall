package cn.e3mall.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;
/**
 * 从索引库中查询商品
 * @author fudin
 *
 */
@Repository
public class SearchDao {
	
	@Autowired
	private SolrServer solrServer;
	
	public SearchResult search(SolrQuery query) throws Exception{
		QueryResponse queryResponse = solrServer.query(query);
		SolrDocumentList documentList = queryResponse.getResults();
		//获得查询总记录数
		long recourdCount = documentList.getNumFound();
		//建立SearchResult
		SearchResult searchResult = new SearchResult();
		//设置参数recourdCount
		searchResult.setRecourdCount((int) recourdCount);
		//创建SearchItemList
		List<SearchItem> itemList = new ArrayList<>();
		//取高亮结果集
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		for(SolrDocument solrDocument : documentList){
			//取商品信息
			SearchItem searchItem = new SearchItem();
			searchItem.setId((String) solrDocument.get("id"));
			
			String item_title = "";
			//取高亮显示的item_title
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			if(list!=null && list.size()>0){
				item_title = list.get(0);
			}else{
				item_title = (String) solrDocument.get("item_title");
			}
			
			searchItem.setTitle(item_title);
			searchItem.setImage((String) solrDocument.get("item_image"));
			searchItem.setPrice((long) solrDocument.get("item_price"));
			searchItem.setSell_point((String) solrDocument.get("item_sell_point"));
			searchItem.setCategory_name((String) solrDocument.get("item_category_name"));
			//将查询结果添加到itemList集合中
			itemList.add(searchItem);
		}
		//把itemList添加到返回结果对象中
		searchResult.setItemList(itemList);
		return searchResult;
	}
	
}
