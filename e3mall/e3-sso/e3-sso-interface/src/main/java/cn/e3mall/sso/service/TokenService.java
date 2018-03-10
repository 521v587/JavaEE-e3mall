package cn.e3mall.sso.service;

import cn.e3mall.common.utils.E3Result;

public interface TokenService {

	//token取用户信息
	E3Result getUserByToken(String token);
}
