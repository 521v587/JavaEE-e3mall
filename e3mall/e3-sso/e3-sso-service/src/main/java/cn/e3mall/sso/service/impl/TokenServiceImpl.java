package cn.e3mall.sso.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;
/**
 * 根据token从redis中取出用户信息
 * @author fudin
 *
 */
@Service
public class TokenServiceImpl implements TokenService {
	@Autowired
	private JedisClient jedisClient;
	@Value("${REDIS_KEY_PRE}")
	private String REDIS_KEY_PRE;
	@Value("${TOKEN_EXPIRE}")
	private int TOKEN_EXPIRE;

	@Override
	public E3Result getUserByToken(String token) {
		//根据token从redis中取出用户的信息
		String json = jedisClient.get(REDIS_KEY_PRE + ":" + token);
		if(StringUtils.isBlank(json)){
			//如果json为空，则代表用户的token已过期
			return E3Result.build(400, "用户已过期，请重新登录");
		}
		//如果json不为空，充值redis中的过期时间
		jedisClient.expire(REDIS_KEY_PRE + ":" + token, TOKEN_EXPIRE);
		//将json数据转化为user对象，E3Result包装返回
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		return E3Result.ok(user);
	}

}
