package cn.e3mall.sso.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.LoginService;
/**
 * 用户登录
 * @author fudin
 *
 */
@Controller
public class LoginServiceImpl implements LoginService {
	@Autowired
	private TbUserMapper tbUserMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${TOKEN_EXPIRE}")
	private int TOKEN_EXPIRE;
	@Value("${REDIS_KEY_PRE}")
	private String REDIS_KEY_PRE;

	@Override
	public E3Result userLogin(String username, String password) {
		
		//1.判断有没有用户名
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = tbUserMapper.selectByExample(example);
		//2.判断用户名有没有注册
		if(list == null || list.size() == 0){
			//用户名没用注册
			return E3Result.build(400, "用户名或密码错误");
		}
		
		TbUser user = list.get(0);
		//3.判断用户名和密码是否匹配
		if(!list.get(0).getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))){
			//密码错误
			return E3Result.build(400, "用户名或密码错误");
		}
		//4.生成token
		String token = UUID.randomUUID().toString();
		
		//5.将token和用户信息写入redis中
		//将密码设置为Null，防止密码泄露
		user.setPassword(null);
		jedisClient.set(REDIS_KEY_PRE + ":" + token, JsonUtils.objectToJson(user));
		//设置token过期时间
		jedisClient.expire(REDIS_KEY_PRE + ":" + token, TOKEN_EXPIRE);
		//6.将token返回
		return E3Result.ok(token);
	}

}
