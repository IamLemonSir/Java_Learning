package exchange.direct;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Direct Exchange Consumer
 */
public class DirectConsumer {

    public static final String DLX_EXCHANGE_NAME = "dlx_accept";

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
        // 队列 声明时绑定死信交换器
        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        map.put("x-message-ttl", 10000);
        channel.queueDeclare(queueName, false, false, false, map);

        // 声明路由键
        String routingKey = "love";

        // 队列绑定交换器
        channel.queueBind(queueName, DirectProducer.EXCHANGE_NAME, routingKey);

        // 定义消费者
        final Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
               try {
                   String message = new String(body, "utf-8");
                   System.out.println("收到的消息:" + message + ", 路由键为:" + envelope.getRoutingKey());
                   logger.info("消息进入队列时间" + System.currentTimeMillis());
                   // 参数：DeliveryTag（消息唯一标识符）,multiple: 是否批量
                   // int i = 1 / 0;
                   // channel.basicAck(envelope.getDeliveryTag(), false);
               }catch (Exception e){
                   // 消息拒绝后，投入死信交换器
                   logger.info("拒绝消息：" + new String(body, "utf-8"));
                   // channel.basicReject(envelope.getDeliveryTag(), false);
               }
            }
        };

        // 消费者消费消息, false表示开启手动确认模式
        // channel.basicConsume(queueName, false, consumer);
    }
}
