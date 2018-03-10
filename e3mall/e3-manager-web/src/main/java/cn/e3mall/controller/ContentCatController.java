package cn.e3mall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCatService;

@Controller
public class ContentCatController {
	@Autowired
	private ContentCatService contentCatService;
	
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContetnCatList(
			@RequestParam(value="id", defaultValue="0") long parentId){
		List<EasyUITreeNode> list = contentCatService.getContentCatList(parentId);
		return list;
	}

	/**
	 * 添加商品分类结点
	 */
	@RequestMapping(value="/content/category/create", method=RequestMethod.POST)
	@ResponseBody
	public E3Result addContentCategory(Long parentId, String name){
		//调用添加结点操作
		E3Result result = contentCatService.addContentCategory(parentId, name);
		return result;
	}
	
	/**
	 * 更新节点
	 */
	@RequestMapping(value="/content/category/update", method=RequestMethod.POST)
	@ResponseBody
	public E3Result updateContentCategory(Long id, String name){
		//调用更新结点操作
		E3Result result = contentCatService.updateContentCategory(id, name);
		return result;
	}
	/**
	 * 删除结点
	 */
	@RequestMapping(value="/content/category/delete", method=RequestMethod.POST)
	@ResponseBody
	public void deleteContentCategory(Long id){
		//调用删除结点操作
		contentCatService.deleteContentCategory(id);
	}
}
