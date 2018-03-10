package cn.e3mall.sso.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.sso.service.TokenService;

/**
 * 判断token是否存在，显示页面登陆的用户
 * @author fudin
 *
 */
@Controller
public class TokenController {
	
	@Autowired
	private TokenService tokenService;
	/**
	 * 返回的是jsonp数据，进行跨域请求
	 * @param token
	 * @param callback
	 * @return
	 */
/*	@RequestMapping(value="/user/token/{token}", produces=MediaType.APPLICATION_JSON_UTF8_VALUE"application/json;charset=utf-8")
	@ResponseBody
	public String getUserByToken(@PathVariable String token, String callback){
		E3Result result = tokenService.getUserByToken(token);
		if(StringUtils.isNotBlank(callback)){
			return callback + "(" + JsonUtils.objectToJson(result) + ");";
		}
		
		return JsonUtils.objectToJson(result);
	}*/
	@RequestMapping(value="/user/token/{token}")
	@ResponseBody
	public Object getUserByToken(@PathVariable String token, String callback){
		E3Result result = tokenService.getUserByToken(token);
		if(StringUtils.isNotBlank(callback)){
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}
		
		return result;
	}

}
