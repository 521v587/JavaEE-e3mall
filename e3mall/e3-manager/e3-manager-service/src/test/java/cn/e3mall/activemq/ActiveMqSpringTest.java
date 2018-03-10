package cn.e3mall.activemq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class ActiveMqSpringTest {

	/**
	 * 测试activemq-spring整合方法
	 * @throws Exception
	 */
	@Test
	public void testActiveMqSpring() throws Exception{
		//初始化一个Spring容器
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
		//从Spring容器中取出JmsTemplate对象
		JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class);
		//从Spring容器中取出Destination对象
		Queue queue = (Queue) applicationContext.getBean("queueDestination");
		//发送消息，JMSTemplate需要知道Queue对象
		jmsTemplate.send(queue, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage("hello jms queue");
				return textMessage;
			}
		});
	}
}
