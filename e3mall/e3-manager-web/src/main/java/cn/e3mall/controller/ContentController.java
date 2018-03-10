package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;

@Controller
public class ContentController {
	@Autowired
	private ContentService contentService;
	
	/**
	 * 查询Content列表信息
	 */
	@RequestMapping(value="/content/query/list")
	@ResponseBody
	public EasyUIDataGridResult getContentList(int page, int rows){
		EasyUIDataGridResult result = contentService.getContentList(page, rows);
		return result;
	}
	
	/**
	 * 增加Content数据
	 */
	@RequestMapping(value="/content/save")
	@ResponseBody
	public E3Result addContent(TbContent content){
		E3Result result = contentService.addContent(content);
		return result;
	}
}
