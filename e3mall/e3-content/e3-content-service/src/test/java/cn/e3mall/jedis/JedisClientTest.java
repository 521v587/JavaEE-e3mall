package cn.e3mall.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.e3mall.common.jedis.JedisClient;

public class JedisClientTest {
	@Test
	public void testJedisClient() throws Exception{
		//初始化Spring容器
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		//从容器中获得jedisClientPool对象
		JedisClient jedisClient = applicationContext.getBean(JedisClient.class);
		//读取数据
		jedisClient.set("clientCluster2", "clientCluster2");
		String testClient = jedisClient.get("clientCluster2");
		System.out.println(testClient);
	}
}
