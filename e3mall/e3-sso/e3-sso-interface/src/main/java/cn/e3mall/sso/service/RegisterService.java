package cn.e3mall.sso.service;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;

public interface RegisterService {
	//注册页面校验数据
	E3Result checkData(String param, int type);
	//注册写入数据库
	E3Result register(TbUser tbUser);
}
