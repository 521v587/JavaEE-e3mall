package cn.e3mall.publish;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PublishTest {

	/**
	 * Service层并没有http请求调用tomcat方法，tomcat只是初始化了一个Spring容器
	 * 我们采用以下的这种方式同样可以初始化一个Spring容器
	 * @throws Exception
	 */
/*	@Test
	public void testPublish() throws Exception{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*");
		System.out.println("服务启动");
		System.in.read();
		System.out.println("服务关闭");
		
	}*/
}
