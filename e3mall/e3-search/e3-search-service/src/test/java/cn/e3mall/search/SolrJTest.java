package cn.e3mall.search;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrJTest {

	@Test
	public void addDocument() throws Exception{
		//创建一个SolrServer对象
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr/collection1");
		//创建一个文档对象SolrInputDocument
		SolrInputDocument document = new SolrInputDocument();
		//向文件中添加域，文件中必须包括一个id域，所有域必须在schema.xml中定义
		document.addField("id", "测试id");
		document.addField("item_title", "测试商品");
		document.addField("item_price", 1004);
		//把文档写入索引库
		solrServer.add(document);
		//提交
		solrServer.commit();
	}
	
	@Test
	public void deleteDocument() throws Exception{
		//创建一个SolrServer对象
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr/collection1");
		//删除文档
		//solrServer.deleteById("测试id");
		solrServer.deleteByQuery("id:测试id");
		solrServer.commit();
	}
	
	//查询文档，简单查询
	@Test
	public void queryDocument() throws Exception{
		//创建一个SolrServer对象
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr/collection1");
		//创建一个查询对象SolrQuery，根据solr页面填写查询条件
		SolrQuery query = new SolrQuery();
		//设置查询条件
		//query.setQuery("item_title:三星");
		query.set("q", "*:*");
		//执行查询，QueryResponse对象
		QueryResponse queryResponse = solrServer.query(query);
		//取文档列表，取查询总记录数
		SolrDocumentList documentList = queryResponse.getResults();
		//遍历
		for(SolrDocument document : documentList){
			System.out.println(document.get("id"));
			System.out.println(document.get("item_title"));
			System.out.println(document.get("item_sell_point"));
			System.out.println(document.get("item_price"));
			System.out.println(document.get("item_image"));
			System.out.println(document.get("item_category_name"));
		}
	}
	
	//查询文档，复杂查询
	@Test
	public void queryMultiDocument() throws Exception{
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr/collection1");
		SolrQuery query = new SolrQuery();
		//设置查询域
		String queryField = "item_title";
		query.set("df",queryField);
		query.setQuery("手机");
		query.setStart(0);
		query.setRows(20);
		//设置高亮
		query.setHighlight(true);
		query.setHighlightSimplePre("<em>");
		query.setHighlightSimplePost("</em>");
		//执行查询
		QueryResponse queryResponse = solrServer.query(query);
		//获得查询结果集
		SolrDocumentList documentList = queryResponse.getResults();
		//查询的总记录数
		System.out.println("查询的总记录数" + documentList.getNumFound());
		//取高亮显示部分
		//JSON : "1305682": {"item_title": [ "TCL 老人<em>手机</em> (i310+) 纯净白 移动联通2G<em>手机</em>" ]}
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		for(SolrDocument document : documentList){
			String title = "";
			List<String> list = highlighting.get(document.get("id")).get(queryField);
			if(list!=null && list.size()>0){
				title = list.get(0);
			}else{
				title = (String) document.get("item_title");
			}
			System.out.println(document.get("id"));
			System.out.println(title);
			System.out.println(document.get("item_sell_point"));
			System.out.println(document.get("item_price"));
			System.out.println(document.get("item_image"));
			System.out.println(document.get("item_category_name"));
		}
	}
}
