package cn.e3mall.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.service.SearchService;

@Controller
public class SearchController {

	@Autowired
	private SearchService searchService;
	@Value("${PAGE_ROWS}")
	private int PAGE_ROWS;
	
	@RequestMapping("/search")
	public String search(String keyword, 
			@RequestParam(defaultValue="1") Integer page ,Model model) throws Exception{
		//搜索后向页面回显数据乱码
		keyword = new String(keyword.getBytes("ISO8859-1"),"UTF-8");
		//调SearchService查询商品信息
		SearchResult searchResult = searchService.search(keyword, page, PAGE_ROWS);
		//测试全局异常处理器
		//int i = 1/0;
		//向页面中回显数据
		model.addAttribute("recourdCount", searchResult.getRecourdCount());
		model.addAttribute("itemList", searchResult.getItemList());
		model.addAttribute("totalPages", searchResult.getTotalPages());
		model.addAttribute("query", keyword);
		model.addAttribute("page", page);
		//返回逻辑视图
		return "search";
	}
	
	
}
