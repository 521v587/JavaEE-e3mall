package cn.e3mall.search.service;

import cn.e3mall.common.pojo.SearchResult;

/**
 * 从索引库中查询商品
 * @author fudin
 *
 */
public interface SearchService {
	SearchResult search(String keyword, int page, int rows) throws Exception;
}
