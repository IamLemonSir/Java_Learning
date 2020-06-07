package exchange.dlx;

import com.rabbitmq.client.*;
import exchange.direct.DirectConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 死信交换器的消费者
 */
public class DlxConsumer {

    private static Logger  logger = LoggerFactory.getLogger(DlxConsumer.class);

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.241.132");
        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        channel.exchangeDeclare(DirectConsumer.DLX_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String queueName = "dlx-accept";

        // 参数：队列名，是否持久化，是否是消费者独占队列(队列只能有一个消费者)，是否是自动删除队列(当消费者都断开连接后，队列会被删除)，队列添加额外属性的map

        channel.queueDeclare(queueName, false, false, false, null);

        channel.queueBind(queueName, DirectConsumer.DLX_EXCHANGE_NAME, "#");

        final Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                logger.info("时间：" + System.currentTimeMillis());
                String message = new String(body, "utf-8");
                logger.info("死信交换器获取消息" + message + ", 路由键：" + envelope.getRoutingKey());
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(queueName, false, consumer);
    }
}
