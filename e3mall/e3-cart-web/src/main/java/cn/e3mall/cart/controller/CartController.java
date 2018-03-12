package cn.e3mall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;

/**
 * 购物车处理Controller
 * @author fudin
 *
 */
@Controller
public class CartController {
	@Autowired
	private ItemService itemService;
	@Value("${COOKIE_CART_EXPIER}")
	private int COOKIE_CART_EXPIER;
	@Autowired
	private CartService cartService;

	/**
	 * 商品添加到购物车
	 */
	@RequestMapping("/cart/add/{itemId}")
	public String addCartItem(@PathVariable Long itemId, @RequestParam(defaultValue="1") Integer num,
			HttpServletRequest request, HttpServletResponse response){
		TbUser user = (TbUser) request.getAttribute("user");
		if(user != null){
			//当用户登录时，添加商品到购物车
			cartService.addCart(user.getId(), itemId, num);
			return "cartSuccess";
		}
		//从Cookie中查询商品列表
		List<TbItem> cartList = getCartFromCookie(request);
		//设置一个标志，根据此标志判断购物车中有没有此商品
		boolean flag = false;
		//根据商品id判断购物车中有没有此商品
		for(TbItem tbItem : cartList){
			//比较的是值
			if(tbItem.getId().longValue() == itemId){
				//购物车中有此商品，数量相加
				tbItem.setNum(tbItem.getNum() + num);
				flag = true;
				break;
			}
		}
		if(!flag){
			//没有的话查询数据库，返回TBItem对象
			//添加到List<TbItem>集合中
			TbItem tbItem = itemService.getItemById(itemId);
			//只取出一张图片即可
			if(StringUtils.isNoneBlank(tbItem.getImage())){
				tbItem.setImage(tbItem.getImage().split(",")[0]);
			}
			//设置购买数量
			tbItem.setNum(num);
			cartList.add(tbItem);
		}
		//写入cookie
		String json = JsonUtils.objectToJson(cartList);
		//json编码（可能含有汉字），并设置过期时间
		CookieUtils.setCookie(request, response, "cart", json, COOKIE_CART_EXPIER, true);
		//返回逻辑视图
		return "cartSuccess";
	}
	
	/**
	 * 从Cookie中取出商品列表的处理
	 */
	private List<TbItem> getCartFromCookie(HttpServletRequest request){
		String json = CookieUtils.getCookieValue(request, "cart", true);
		//如果json为空，返回一个空的List集合
		if(StringUtils.isBlank(json)){
			return new ArrayList<>();
		}else{
			List<TbItem> tbItemList = JsonUtils.jsonToList(json, TbItem.class);
			return tbItemList;
		}
	}
	
	/**
	 * 展示购物车页面
	 */
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request, HttpServletResponse response, Model model){
		//判断用户是否登录
		TbUser user = (TbUser) request.getAttribute("user");
		//从cookie中取出购物车对象
		List<TbItem> cartList = getCartFromCookie(request);
		if(user != null){
			//如果cookie中存在数据，则与redis中缓存的购物车对象合并
			cartService.mergeCart(user.getId(), cartList);
			//删除cookie
			CookieUtils.deleteCookie(request, response, "cart");
			//登录时从redis缓存中读取购物车对象
			cartList = cartService.getCartList(user.getId());
		}
		//如果未登录放在request域中
		model.addAttribute("cartList", cartList);
		return "cart";
	}
	
	/**
	 * 更新购物车商品数量
	 */
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public E3Result updateCartNum(@PathVariable Long itemId, @PathVariable int num,
			HttpServletRequest request, HttpServletResponse response){
		//如果用户登录时
		TbUser user = (TbUser) request.getAttribute("user");
		if(user != null){
			cartService.updateCartItemNum(user.getId(), itemId, num);
			return E3Result.ok();
		}
		//用户没有登录，从Cookie中查询商品列表
		List<TbItem> cartList = getCartFromCookie(request);
		for(TbItem tbItem : cartList){
			if(tbItem.getId().longValue() == itemId){
				tbItem.setNum(num);
				break;
			}
		}
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIER, true);
		return E3Result.ok();
	}
	
	/**
	 * 删除商品
	 */
	@RequestMapping("/cart/delete/{itemId}")
	public String delectCartItem(@PathVariable  Long itemId, 
			HttpServletRequest request, HttpServletResponse response){
		//如果用户登录
		TbUser user = (TbUser) request.getAttribute("user");
		if(user != null){
			cartService.delectCartItem(user.getId(), itemId);
			return "redirect:/cart/cart.html";
		}
		//从cookie中获取cart集合
		List<TbItem> cartList = getCartFromCookie(request);
		//遍历cart集合，删除该商品
		for(TbItem tbItem : cartList){
			if(tbItem.getId().longValue() == itemId){
				cartList.remove(tbItem);
				break;
			}
		}
		//更新cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIER, true);
		//返回重定向的逻辑视图
		return "redirect:/cart/cart.html";
	}
		
}
