package cn.e3mall.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;

@Controller
public class OrderController {
	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;
	/**
	 * 显示结算页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/order/order-cart")
	public String showOrder(HttpServletRequest request){
		//获得user
		TbUser user = (TbUser) request.getAttribute("user");
		//获得购物车商品列表对象
		List<TbItem> cartList = cartService.getCartList(user.getId());
		//把商品列表传递给jsp
		request.setAttribute("cartList", cartList);
		return "order-cart";
	}
	/**
	 * 创建订单
	 * @return
	 */
	@RequestMapping("/order/create")
	public String createOrder(OrderInfo orderInfo, HttpServletRequest request){
		//获得user
		TbUser user = (TbUser) request.getAttribute("user");
		//补全商品信息
		orderInfo.setUserId(user.getId());
		orderInfo.setBuyerNick(user.getUsername());
		//调用service创建order
		E3Result result = orderService.createOrder(orderInfo);
		//向页面传入数据
		String orderId = result.getData().toString();
		request.setAttribute("orderId", orderId);
		request.setAttribute("payment", orderInfo.getPayment());
		return "success";
	}

}
