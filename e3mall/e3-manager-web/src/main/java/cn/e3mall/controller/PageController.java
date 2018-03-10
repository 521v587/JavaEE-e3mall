package cn.e3mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面管理
 * @author fudin
 *
 */
@Controller
public class PageController {
	
	@RequestMapping("/")
	public String pageIndex(){
		return "index";
	}
	/*
	 * 从前台jsp页面传入的jsp页面名字取值page
	 * 跳转到相应的jsp页面
	 */
	@RequestMapping("/{page}")
	public String showPage(@PathVariable String page){
		return page;
	}
}
