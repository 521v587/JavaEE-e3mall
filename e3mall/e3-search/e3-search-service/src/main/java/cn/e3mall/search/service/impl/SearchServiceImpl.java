package cn.e3mall.search.service.impl;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.dao.SearchDao;
import cn.e3mall.search.service.SearchService;
@Service
public class SearchServiceImpl implements SearchService {
	@Autowired
	private SearchDao searchDao;
	@Value("${DEFAULT_FIELD}")
	private String DEFAULT_FIELD;

	@Override
	public SearchResult search(String keyword, int page, int rows) throws Exception {
		SolrQuery solrQuery = new SolrQuery();
		
		//设置查询域
		solrQuery.set("df", DEFAULT_FIELD);
		//设置查询关键字
		solrQuery.setQuery(keyword);
		//设置分页
		solrQuery.setStart((page - 1) * rows);
		solrQuery.setRows(rows);
		//设置高亮显示
		solrQuery.setHighlight(true);
		solrQuery.setHighlightSimplePre("<em style=\"color:red\">");
		solrQuery.setHighlightSimplePost("</em>");
		
		//执行查询
		SearchResult searchResult = searchDao.search(solrQuery);
		
		//设置返回结果的总页数totalPages
		int recourdCount = searchResult.getRecourdCount();
		int totalPages = recourdCount / rows;
		if(recourdCount % rows != 0){
			totalPages++;
		}
		searchResult.setTotalPages(totalPages);
		
		return searchResult;
	}

}
