package cn.e3mall.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
@Service
public class CartServiceImpl implements CartService {
	
	@Value("${REDIS_KEY_PRE}")
	private String REDIS_KEY_PRE;
	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private TbItemMapper itemMapper;

	/**
	 * 登录时添加商品
	 */
	@Override
	public E3Result addCart(long userId, long itemId, int num) {
		//向redis中添加购物车
		//数据类型为hash，key:userID field:itemID value：商品信息
		//判断购物车中是否含有该商品
		Boolean hexists = jedisClient.hexists(REDIS_KEY_PRE + ":" + userId, itemId + "");
		if(hexists){
			//如果有，该商品的数量相加
			String json = jedisClient.hget(REDIS_KEY_PRE + ":" + userId, itemId + "");
			TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
			tbItem.setNum(tbItem.getNum() + num);
			jedisClient.hset(REDIS_KEY_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
			return E3Result.ok();
		}
		//如果没有，添加到商品列表中
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		//设置商品数量
		tbItem.setNum(num);
		//取一张图片
		tbItem.setImage(tbItem.getImage().split(",")[0]);
		//存到缓存中
		jedisClient.hset(REDIS_KEY_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
		return E3Result.ok();
	}

	/**
	 * 合并cookie中的cart和redis中的cart
	 */
	@Override
	public E3Result mergeCart(long userId, List<TbItem> itemList) {
		//遍历列表商品
		for(TbItem item : itemList){
			//把列表添加到购物车
			//如果购物车中有此商品，数量相加
			//如果没有，添加到购物车
			addCart(userId, item.getId(), item.getNum());
		}
		//返回成功
		return E3Result.ok();
	}

	/**
	 * 展示redis中的cart
	 */
	@Override
	public List<TbItem> getCartList(long userId) {
		//获得redis中的cart
		List<String> list = jedisClient.hvals(REDIS_KEY_PRE + ":" + userId);
		//将List<String>转化成List<TbItem>
		List<TbItem> itemList = new ArrayList<>();
		for(String str : list){
			TbItem item = JsonUtils.jsonToPojo(str, TbItem.class);
			itemList.add(item);
		}
		return itemList;
	}

	/**
	 * 更新购物车中的商品数量
	 */
	@Override
	public E3Result updateCartItemNum(long userId, long itemId, int num) {
		//查询购物车中的商品
		String json = jedisClient.hget(REDIS_KEY_PRE + ":" + userId, itemId + "");
		
		//更改商品的数量
		TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
		item.setNum(num);
		//重新放到redis中
		jedisClient.hset(REDIS_KEY_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(item));
		return E3Result.ok();
	}

	/**
	 * 删除购物车中的商品信息
	 */
	@Override
	public E3Result delectCartItem(long userId, long itemId) {
		//根据用户id和商品id删除
		jedisClient.hdel(REDIS_KEY_PRE + ":" + userId, itemId + "");
		return E3Result.ok();
	}
}
