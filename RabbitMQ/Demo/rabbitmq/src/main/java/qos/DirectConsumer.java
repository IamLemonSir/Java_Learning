package qos;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Direct Exchange Consumer
 */
public class DirectConsumer {

    private static Logger logger = LoggerFactory.getLogger(DirectConsumer.class);

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建与rabbitmq的连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.241.132");
        Connection connection = connectionFactory.newConnection();

        // 创建信道
        final Channel channel = connection.createChannel();

        // 声明交换器
        channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 声明队列
        String queueName = "directHandler";
        channel.queueDeclare(queueName, false, false, false, null);

        // 声明路由键
        String routingKey = "love";

        // 队列绑定交换器
        channel.queueBind(queueName, DirectProducer.EXCHANGE_NAME, routingKey);

        // 定义消费者
        final Consumer consumer = new DefaultConsumer(channel){
            int count = 0;
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "utf-8");
                System.out.println("收到的消息:" + message + ", 路由键为:" + envelope.getRoutingKey());
                count++;
                // 参数：DeliveryTag（消息唯一标识符）,multiple: 是否批量
                if(count % 50 == 0){
                    channel.basicAck(envelope.getDeliveryTag(), true);
                    logger.info("消息批量确认");
                }
            }
        };

        // 设置预取模式
        channel.basicQos(50, true);
        // 消费者消费消息, false表示开启手动确认模式
        channel.basicConsume(queueName, false, consumer);
    }
}
