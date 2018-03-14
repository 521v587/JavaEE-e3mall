package cn.e3mall.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;

public class LoginInterceptor implements HandlerInterceptor {
	
	@Value("${SSO_URL}")
	private String SSO_URL;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private CartService cartService;
	
	/**
	 * 订单拦截器
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//根据token判断用户是否登录
		String token = CookieUtils.getCookieValue(request, "token");
		if(StringUtils.isBlank(token)){
			//如果token为空，则跳转到登录页面
			//response.sendRedirect("http://localhost:8088/page/login");
			response.sendRedirect(SSO_URL + "/page/login?redirectUrl=" + request.getRequestURL());
			return false;
		}
		//如果token不为空，根据token查redis中的数据
		E3Result result = tokenService.getUserByToken(token);
		if(result.getStatus() != 200){
			//如果查不到数据，则代表token已过期，重定向到登录页面
			response.sendRedirect(SSO_URL + "/page/login?redirectUrl=" + request.getRequestURL());
			return false;
		}
		//如果查到数据，则合并web端的cart和redis中的cart对象
		TbUser user = (TbUser) result.getData();
		request.setAttribute("user", user);
		String json = CookieUtils.getCookieValue(request, "cart", true);
		if(StringUtils.isNoneBlank(json)){
			cartService.mergeCart(user.getId(), JsonUtils.jsonToList(json, TbItem.class));
		}
		//返回订单列表页面，放行
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		// TODO Auto-generated method stub

	}


}
