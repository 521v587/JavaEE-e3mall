package cn.e3mall.cart.service;

import java.util.List;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;

public interface CartService {
	//用户登录时，添加商品到购物车
	E3Result addCart(long userId, long itemId, int num);
	//合并cookie中的购物车和redis中的购物车
	E3Result mergeCart(long userId, List<TbItem> itemList);
	//遍历购物车列表
	List<TbItem> getCartList(long userId);
	//更新购物车商品的数量
	E3Result updateCartItemNum(long userId, long itemId, int num);
	//删除商品
	E3Result delectCartItem(long userId, long itemId);
}
