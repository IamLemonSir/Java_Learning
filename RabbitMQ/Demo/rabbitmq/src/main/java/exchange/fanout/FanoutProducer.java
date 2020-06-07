package exchange.fanout;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Fanout Exchange producer
 */
public class FanoutProducer {

    public static final String EXCHANGE_NAME = "fanout_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.241.132");
        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        String queueName = "fanoutHandler3";

        channel.queueDeclare(queueName, false, false, false, null);

        String[] routingKeys = {"love", "marry"};

        for (int i = 0; i < 2; i++){
            String message = "hello world_" + i;
            channel.queueBind(queueName, EXCHANGE_NAME, routingKeys[i]);
            channel.basicPublish(EXCHANGE_NAME, routingKeys[i], null, message.getBytes());
            System.out.println("send: message_" + message + ", routingKey_" + routingKeys[i]);
        }

        channel.close();
        connection.close();
    }
}
