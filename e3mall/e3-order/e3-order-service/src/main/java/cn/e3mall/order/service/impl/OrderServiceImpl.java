package cn.e3mall.order.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbOrderItemMapper;
import cn.e3mall.mapper.TbOrderMapper;
import cn.e3mall.mapper.TbOrderShippingMapper;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbOrderItem;
/**
 * 创建订单服务
 * @author fudin
 *
 */
@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${ORDER_ID_GEN_KEY}")
	private String ORDER_ID_GEN_KEY;
	@Value("${ORDER_ID_START}")
	private String ORDER_ID_START;
	@Value("${ORDER_DETILE_ID_GEN_KEY}")
	private String ORDER_DETILE_ID_GEN_KEY;

	@Override
	public E3Result createOrder(OrderInfo orderInfo) {
		//1.接收表单传入的数据
		//2.生成订单id，采用redis中的incr生成
		if(!jedisClient.exists(ORDER_ID_GEN_KEY)){
			//设置初始值
			jedisClient.set(ORDER_ID_GEN_KEY, ORDER_ID_START);
		}
		String orderId = jedisClient.incr(ORDER_ID_GEN_KEY).toString();
		//3.补全信息,向订单表中插入数据
		orderInfo.setOrderId(orderId);
		//状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭',
		orderInfo.setStatus(1);
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		orderMapper.insert(orderInfo);
		
		//4.向订单商品表中插入数据
		for(TbOrderItem orderItem : orderInfo.getOrderItems()){
			//生成明细id
			String orderItemId = jedisClient.incr(ORDER_DETILE_ID_GEN_KEY).toString();
			orderItem.setId(orderItemId);
			orderItem.setOrderId(orderId);
			//向订单商品表中插入商品数据
			orderItemMapper.insert(orderItem);
		}
		
		//5.向订单物流表中插入数据
		orderInfo.getOrderShipping().setOrderId(orderId);
		orderInfo.getOrderShipping().setCreated(new Date());
		orderInfo.getOrderShipping().setUpdated(new Date());
		orderShippingMapper.insert(orderInfo.getOrderShipping());
		
		//6.返回订单id
		return E3Result.ok(orderId);
	}

}
