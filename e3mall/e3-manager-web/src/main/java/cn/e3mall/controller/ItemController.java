package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.service.ItemService;

/**
 * 商品管理Controller
 * @author 丁楠
 *
 */
@Controller
public class ItemController {
	@Autowired
	private ItemService itemService;
	
	@RequestMapping(value="/item/{itemId}")
	@ResponseBody 
	public TbItem getItemById(@PathVariable Long itemId){
		TbItem tbItem = itemService.getItemById(itemId);
		return tbItem;
	}
	
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page, Integer rows){
		EasyUIDataGridResult result = itemService.getItemList(page, rows);
		return result;
	}
	
	/**
	 * 商品添加
	 */
	@RequestMapping(value="/item/save", method=RequestMethod.POST)
	@ResponseBody
	public E3Result addItem(TbItem item, String desc){
		E3Result result = itemService.addItem(item, desc);
		return result;
	}
	
	/**
	 * 删除商品
	 */
	@RequestMapping(value="/rest/item/delete", method=RequestMethod.POST)
	@ResponseBody
	public E3Result deleteItem(long[] ids){
		E3Result result = itemService.deleteItem(ids);
		return result;
	}
	
	/**
	 * 上架商品
	 */
	@RequestMapping(value="/rest/item/reshelf", method=RequestMethod.POST)
	@ResponseBody
	public E3Result upStatus(long[] ids){
		E3Result result = itemService.upStatus(ids);
		return result;
	}
	/**
	 * 下架商品
	 */
	@RequestMapping(value="/rest/item/instock", method=RequestMethod.POST)
	@ResponseBody
	public E3Result downStatus(long[] ids){
		E3Result result = itemService.downStatus(ids);
		return result;
	}
	
	/**
	 * 回显数据的itemDesc
	 */
	@RequestMapping("/rest/item/query/item/desc/{id}")
	@ResponseBody
	public E3Result getItemDesc(@PathVariable Long id){
		E3Result result = itemService.getItemDesc(id);
		return result;
	}
	/**
	 * 回显数据的itemParam
	 */
	@RequestMapping("/rest/item/param/item/query/{id}")
	@ResponseBody
	public E3Result getItemParam(@PathVariable Long id){
		E3Result result = itemService.getItemParam(id);
		return result;
	}
	
	/**
	 * 回显数据
	 */
	
	
}
