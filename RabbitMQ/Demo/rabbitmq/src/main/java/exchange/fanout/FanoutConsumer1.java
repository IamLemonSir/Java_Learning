package exchange.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Fanout Exchange consumer
 */
public class FanoutConsumer1 {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.241.132");
        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        channel.exchangeDeclare(FanoutProducer.EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        String queueName = "fanoutHandler11";

        channel.queueDeclare(queueName, false, false, false, null);

        String routingKey = "love";

        channel.queueBind(queueName, FanoutProducer.EXCHANGE_NAME, routingKey);

        final Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "utf-8");
                System.out.println("消费消息" + message + ", 路由键：" + envelope.getRoutingKey());
            }
        };

        channel.basicConsume(queueName, true, consumer);

    }
}
