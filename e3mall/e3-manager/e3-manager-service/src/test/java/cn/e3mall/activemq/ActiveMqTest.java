package cn.e3mall.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class ActiveMqTest {

	/**
	 * Quene点到点形式发送消息
	 * 
	 * @throws Exception
	 */
	@Test
	public void testQueneProducer() throws Exception {
		// 1.创建一个工厂连接对象，需要指定服务的ip和端口号
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		// 2.使用工厂创建一个Connection对象
		Connection connection = connectionFactory.createConnection();
		// 3.开启连接，调用start方法
		connection.start();
		// 4.创建一个Session对象
		// 第一个参数，true代表开启事务，开启事务后第二个参数无意义，一般不开启
		// 第二个参数应答模式，分为手动应答和自动应答，一般为自动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 5.使用Session对象创建一个Destination对象，两种形式Quene和Topic，目前使用Quene
		Queue queue = session.createQueue("test-queue");
		// 6.使用Session创建一个Producer对象
		MessageProducer producer = session.createProducer(queue);
		// 7.创建一个Message对象，可以使用TextMessage
		/*
		 * TextMessage textMessage = new ActiveMQTextMessage();
		 * textMessage.setText("hello quene");
		 */
		TextMessage textMessage = session.createTextMessage("hello queue");
		// 8.发送消息
		producer.send(textMessage);
		// 9.关闭资源
		producer.close();
		session.close();
		connection.close();
	}

	@Test
	public void testQueueConsumer() throws Exception {
		// 1.创建一个工厂连接对象，需要指定服务的ip和端口号
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		// 2.使用工厂创建一个Connection对象
		Connection connection = connectionFactory.createConnection();
		// 3.开启连接，调用start方法
		connection.start();
		// 4.创建一个Session对象
		// 第一个参数，true代表开启事务，开启事务后第二个参数无意义，一般不开启
		// 第二个参数应答模式，分为手动应答和自动应答，一般为自动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//5.使用session创建一个destination对象,quene对象
		Queue queue = session.createQueue("test-queue");
		//6.使用session创建一个consumer对象
		MessageConsumer consumer = session.createConsumer(queue);
		//7.接收消息
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				try {
					TextMessage textMessage = (TextMessage) message;
					String text = textMessage.getText();
					//8.打印结果
					System.out.println(text);
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		//等待键盘输入
		System.in.read();
		//9.关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
	
	/**
	 * Topic发布/订阅模式
	 */
	@Test
	public void testTopicProducer() throws Exception {
		// 1.创建一个工厂连接对象，需要指定服务的ip和端口号
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		// 2.使用工厂创建一个Connection对象
		Connection connection = connectionFactory.createConnection();
		// 3.开启连接，调用start方法
		connection.start();
		// 4.创建一个Session对象
		// 第一个参数，true代表开启事务，开启事务后第二个参数无意义，一般不开启
		// 第二个参数应答模式，分为手动应答和自动应答，一般为自动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 5.使用Session对象创建一个Destination对象，两种形式Quene和Topic，目前使用Topic
		Topic topic = session.createTopic("test-topic");
		// 6.使用Session创建一个Producer对象
		MessageProducer producer = session.createProducer(topic);
		// 7.创建一个Message对象，可以使用TextMessage
		/*
		 * TextMessage textMessage = new ActiveMQTextMessage();
		 * textMessage.setText("hello topic");
		 */
		TextMessage textMessage = session.createTextMessage("hello topic");
		// 8.发送消息
		producer.send(textMessage);
		// 9.关闭资源
		producer.close();
		session.close();
		connection.close();
	}

	@Test
	public void testTopicConsumer() throws Exception {
		// 1.创建一个工厂连接对象，需要指定服务的ip和端口号
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		// 2.使用工厂创建一个Connection对象
		Connection connection = connectionFactory.createConnection();
		// 3.开启连接，调用start方法
		connection.start();
		// 4.创建一个Session对象
		// 第一个参数，true代表开启事务，开启事务后第二个参数无意义，一般不开启
		// 第二个参数应答模式，分为手动应答和自动应答，一般为自动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//5.使用session创建一个destination对象,topic对象
		Topic topic = session.createTopic("test-topic");
		//6.使用session创建一个consumer对象
		MessageConsumer consumer = session.createConsumer(topic);
		//7.接收消息
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				try {
					TextMessage textMessage = (TextMessage) message;
					String text = textMessage.getText();
					//8.打印结果
					System.out.println(text);
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		//等待键盘输入
		System.in.read();
		//9.关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
	
}
