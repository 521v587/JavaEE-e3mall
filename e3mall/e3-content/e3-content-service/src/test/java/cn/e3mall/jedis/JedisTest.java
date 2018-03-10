package cn.e3mall.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisTest {

	/**
	 * 测试Jedis
	 */
	@Test
	public void jedisTest(){
		Jedis jedis = new Jedis("192.168.25.128", 6379);
		jedis.set("test", "My First Jedis");
		String test = jedis.get("test");
		System.out.println(test);
		jedis.close();
	}
	/**
	 * 测试JedisPool
	 */
	@Test
	public void jedisPoolTest(){
		//建立连接池
		JedisPool pool = new JedisPool("192.168.25.128", 6379);
		//获取连接池对象
		Jedis jedis = pool.getResource();
		//添加内容
		jedis.set("test2", "My Second Jedis");
		//输入内容
		String test2 = jedis.get("test2");
		System.out.println(test2);
		//把连接池对象放回池子中
		jedis.close();
		//关闭池子
		pool.close();
	}
	/**
	 * JedisCluster
	 */
	@Test
	public void testJedisCluster(){
		//创建一个JedisCluster对象，构造方法中有一个ser集合，set<HostAndPort>
		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		nodes.add(new HostAndPort("192.168.25.128", 7001));
		nodes.add(new HostAndPort("192.168.25.128", 7002));
		nodes.add(new HostAndPort("192.168.25.128", 7003));
		nodes.add(new HostAndPort("192.168.25.128", 7004));
		nodes.add(new HostAndPort("192.168.25.128", 7005));
		nodes.add(new HostAndPort("192.168.25.128", 7006));
		JedisCluster cluster = new JedisCluster(nodes);
		//存储数据，直接使用JedisCluster对象操作Redis
		cluster.set("test3", "My First JedisCluster");
		String test3 = cluster.get("test3");
		System.out.println(test3);
		cluster.close();
	}
}
