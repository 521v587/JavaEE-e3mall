package cn.e3mall.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.RegisterService;
/**
 * 用户注册页面
 * @author fudin
 */
@Service
public class RegisterServiceImpl implements RegisterService {
	@Autowired
	private TbUserMapper tbUserMapper;

	/**
	 * 用户注册页面校验数据
	 */
	@Override
	public E3Result checkData(String param, int type) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		//1.用户名 2.电话号码 3.邮箱，根据type设置查询条件
		if(type == 1){
			criteria.andUsernameEqualTo(param);
		}else if(type == 2){
			criteria.andPhoneEqualTo(param);
		}else if(type == 3){
			criteria.andEmailEqualTo(param);
		}else{
			return E3Result.build(400, "非法参数");
		}
		List<TbUser> list = tbUserMapper.selectByExample(example);
		if(list != null && list.size()>0){
			//如果list中有集合，返回false
			return E3Result.ok(false);
		}
		//如果没有返回true
		return E3Result.ok(true);
	}

	@Override
	public E3Result register(TbUser tbUser) {
		//首先进行是否为空判断
		
		if(StringUtils.isBlank(tbUser.getUsername())){
			return E3Result.build(400, "用户名不能为空");
		}
		if(StringUtils.isBlank(tbUser.getPassword())){
			return E3Result.build(400, "密码不能为空");
		}
		
		//校验用户名是否注册
		//1.用户名 2.电话号码 3.邮箱
		E3Result result = checkData(tbUser.getUsername(), 1);
		if(!(boolean)result.getData()){
			return E3Result.build(400, "此用户名已经被占用");
		}
		//校验手机号是否被注册
		result = checkData(tbUser.getPhone(), 2);
		if(!(boolean)result.getData()){
			return E3Result.build(400, "此手机号已经注册");
		}
		
		//补全tbUser参数
		tbUser.setCreated(new Date());
		tbUser.setUpdated(new Date());
		
		//密码加密
		String password = DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes());
		tbUser.setPassword(password);
		
		//向数据库中插入数据
		tbUserMapper.insert(tbUser);
		
		return E3Result.ok(true);
	}

}
