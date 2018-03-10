package cn.e3mall.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.RegisterService;

/**
 * 注册页面
 * @author fudin
 *
 */
@Controller
public class RegisterController {
	@Autowired
	private RegisterService registerService;

	@RequestMapping("/page/register")
	public String showRegister(){
		return "register";
	}
	/**
	 * 注册参数校验
	 * @param param
	 * @param type
	 * @return
	 */
	@RequestMapping("/user/check/{param}/{type}")
	@ResponseBody
	public E3Result checkData(@PathVariable String param, @PathVariable Integer type){
		E3Result result = registerService.checkData(param, type);
		return result;
	}
	/**
	 * 注册
	 * @param tbUser
	 * @return
	 */
	@RequestMapping("/user/register")
	@ResponseBody
	public E3Result register(TbUser tbUser){
		E3Result result = registerService.register(tbUser);
		return result;
	}
}
