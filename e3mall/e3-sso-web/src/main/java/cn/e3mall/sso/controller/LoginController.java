package cn.e3mall.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.sso.service.LoginService;

@Controller
public class LoginController {
	@Autowired
	private LoginService loginService;
	@Value("${TOKEN_KEY}")
	private String TOKEN_KEY;
	
	@RequestMapping("/page/login")
	public String getLogin(){
		return "login";
	}
	
	@RequestMapping(value="/user/login", method=RequestMethod.POST)
	@ResponseBody
	public E3Result login(String username, String password,
			HttpServletRequest request, HttpServletResponse response){
		E3Result result = loginService.userLogin(username, password);
		//判断登录是否成功
		if(result.getStatus() == 200){
			//获取token
			String token = (String) result.getData();
			//将token写入cookie中
			CookieUtils.setCookie(request, response, TOKEN_KEY, token);
		}
		return result;
	}

}
