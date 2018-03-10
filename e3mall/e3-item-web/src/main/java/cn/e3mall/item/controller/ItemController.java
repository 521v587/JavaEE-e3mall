package cn.e3mall.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.item.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;

/**
 * 页面展示商品详细信息
 * @author fudin
 *
 */
@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	@RequestMapping("item/{itemId}")
	public String getItemInfo(@PathVariable long itemId, Model model){
		TbItem tbItem = itemService.getItemById(itemId);
		TbItemDesc itemDesc = itemService.getItemDescById(itemId);
		//将TBItem对象转化为Item对象
		Item item = new Item(tbItem);
		model.addAttribute("item", item);
		model.addAttribute("itemDesc", itemDesc);
		return "item";
	}
}
