package cn.e3mall.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.mapper.TbItemParamMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemExample.Criteria;
import cn.e3mall.pojo.TbItemParam;
import cn.e3mall.service.ItemService;

/**
 * 商品查询
 * 
 * @author 丁楠
 *
 */
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private TbItemParamMapper itemParamMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	// 使用Resource进行优先id匹配注入
	@Resource
	private Destination topicDestination;
	@Autowired
	private JedisClient jedisClient;

	@Value("${REDIS_ITEM_PRE}")
	private String REDIS_ITEM_PRE;
	@Value("${ITEM_CACHE_EXPIRE}")
	private Integer ITEM_CACHE_EXPIRE;

	// 根据id进行查询
	public TbItem getItemById(long itemId) {
		// 防止查询出错，这里加Try Catch
		try {
			// 查询缓存
			String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":BASE");
			if (StringUtils.isNotBlank(json)) {
				// 有缓存的话，加载缓存中的数据
				return JsonUtils.jsonToPojo(json, TbItem.class);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/*
		 * TbItem tbItem = itemMapper.selectByPrimaryKey(itemId); return tbItem;
		 */
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		// 设置查询条件
		criteria.andIdEqualTo(itemId);
		List<TbItem> itemList = itemMapper.selectByExample(example);
		if (itemList != null && itemList.size() > 0) {
			try {
				// 没有缓存的话查询数据库后将数据放入缓存中
				jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":BASE", JsonUtils.objectToJson(itemList.get(0)));
				// 设置key过期时间
				jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":BASE", ITEM_CACHE_EXPIRE);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return itemList.get(0);
		}
		// 若果查询结果为null或无值，返回null
		return null;
	}

	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		// 设置查询信息
		PageHelper.startPage(page, rows);
		// 执行查询
		TbItemExample itemExample = new TbItemExample();
		List<TbItem> itemList = itemMapper.selectByExample(itemExample);
		// 创建返回对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(itemList);
		// 取分页结果
		PageInfo<TbItem> pageInfo = new PageInfo<>(itemList);
		// 取总记录数
		long total = pageInfo.getTotal();
		result.setTotal(total);
		// 返回页面需要的json格式
		// {total:”2”,rows:[{“id”:”1”,”name”:”张三”},{“id”:”2”,”name”:”李四”}]}
		return result;
	}

	/**
	 * 商品添加
	 */
	@Override
	public E3Result addItem(TbItem item, String desc) {
		// 补充商品id
		// 匿名内部类必须加final
		final long itemId = IDUtils.genItemId();
		item.setId(itemId);
		// 补全item属性
		// 商品状态，1-正常，2-下架，3-删除',
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		// 向表Tb_Item中插入item数据
		itemMapper.insert(item);
		// 新建ItemDesc pojo
		TbItemDesc itemDesc = new TbItemDesc();
		itemDesc.setCreated(new Date());
		itemDesc.setUpdated(new Date());
		itemDesc.setItemDesc(desc);
		itemDesc.setItemId(itemId);
		// 向表Tb_Item_Desc中插入desc数据
		itemDescMapper.insert(itemDesc);

		// 发送添加商品的消息到消息队列中
		jmsTemplate.send(topicDestination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(itemId + "");
				return textMessage;
			}
		});
		// 返回插入状态OK
		return E3Result.ok();
	}

	/**
	 * 删除商品
	 */
	@Override
	public E3Result deleteItem(long[] ids) {
		for (Long id : ids) {
			itemMapper.deleteByPrimaryKey(id);
		}
		return E3Result.ok();
	}

	/**
	 * 回显数据的itemDesc
	 */
	@Override
	public E3Result getItemDesc(long id) {
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(id);
		return E3Result.ok(itemDesc);
	}

	/**
	 * 回显数据的itemParam
	 */
	@Override
	public E3Result getItemParam(long id) {
		TbItemParam itemParam = itemParamMapper.selectByPrimaryKey(id);
		return E3Result.ok(itemParam);
	}

	/**
	 * 上架商品
	 */
	@Override
	public E3Result upStatus(long[] ids) {
		for (long id : ids) {
			TbItem item = itemMapper.selectByPrimaryKey(id);
			if (item.getStatus() != 1) {
				item.setStatus((byte) 1);
				itemMapper.updateByPrimaryKey(item);
			}
		}
		return E3Result.ok();
	}

	/**
	 * 下架商品
	 */
	@Override
	public E3Result downStatus(long[] ids) {
		for (long id : ids) {
			TbItem item = itemMapper.selectByPrimaryKey(id);
			if (item.getStatus() != 2) {
				item.setStatus((byte) 2);
				itemMapper.updateByPrimaryKeySelective(item);
			}
		}
		return E3Result.ok();
	}

	/**
	 * 根据商品id查询商品描述
	 * 
	 * @param itemId
	 * @return
	 */
	@Override
	public TbItemDesc getItemDescById(long itemId) {
		// 防止查询出错，这里加Try Catch
		try {
			// 查询缓存
			String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":DESC");
			if (StringUtils.isNotBlank(json)) {
				// 有缓存的话，加载缓存中的数据
				return JsonUtils.jsonToPojo(json, TbItemDesc.class);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);

		try {
			// 没有缓存的话查询数据库后将数据放入缓存中
			jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":DESC", JsonUtils.objectToJson(tbItemDesc));
			// 设置key过期时间
			jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":DESC", ITEM_CACHE_EXPIRE);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return tbItemDesc;
	}

}
