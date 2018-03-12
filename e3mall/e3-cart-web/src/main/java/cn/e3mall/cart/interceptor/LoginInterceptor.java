package cn.e3mall.cart.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;

public class LoginInterceptor implements HandlerInterceptor {
	
	@Autowired
	private TokenService tokenService;
	
	//请求前拦截
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//判断请求cookie中有没有token
		String token = CookieUtils.getCookieValue(request, "token");
		//没有的话，直接放行
		if(StringUtils.isBlank(token)){
			return true;
		}
		//有的话，查询redis中的值是否过期
		E3Result result = tokenService.getUserByToken(token);
		if(result.getStatus() != 200){
			//redis中没有，代表token过期，放行
			return true;
		}
		//redis中有，获取用户信息，将user放到request中，Controller中通过判断请求中是否包含user对象，放行
		TbUser user = (TbUser) result.getData();
		request.setAttribute("user", user);
		return true;
	}

	//请求后，在返回逻辑视图前拦截
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}



}
