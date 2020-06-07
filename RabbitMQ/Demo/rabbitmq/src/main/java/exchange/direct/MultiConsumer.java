package exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 多个消费者消费同一个队列
 */
public class MultiConsumer {

    private static class ConsumerWorker implements Runnable{

        private Connection connection;

        private String queueName;

        public ConsumerWorker(Connection connection, String queueName) {
            this.connection = connection;
            this.queueName = queueName;
        }

        @Override
        public void run() {
            try {
                Channel channel = connection.createChannel();
                channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
                String routingKey = "love";

                channel.queueDeclare(queueName, false, false, false, null);

                final String consumerName = Thread.currentThread().getName();

                channel.queueBind(queueName, DirectProducer.EXCHANGE_NAME, routingKey);

                final Consumer consumer = new DefaultConsumer(channel){
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        String message = new String(body, "utf-8");
                        System.out.println(consumerName + "消费消息，消息内容：" + message + "，路由键为：" + envelope.getRoutingKey());
                    }
                };

                channel.basicConsume(queueName, true, consumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.241.132");
        Connection connection = factory.newConnection();

        String queueName = "directHandler";

        for(int i = 0; i < 3; i++){
            Thread thread = new Thread(new ConsumerWorker(connection, queueName));
            thread.start();
        }
    }
}
