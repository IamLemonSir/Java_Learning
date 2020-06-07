package exchange.direct;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 使用Direct Exchange
 */
public class DirectProducer {

    private static Logger logger = LoggerFactory.getLogger(DirectProducer.class);

    static final String EXCHANGE_NAME = "direct_test";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建与rabbitmq的连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.241.132");
        Connection connection = factory.newConnection();

        // 创建信道
        Channel channel = connection.createChannel();

        // 声明交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 开启发送端确认模式
        channel.confirmSelect();

        channel.addReturnListener(new ReturnListener() {
            // 参数：返回的状态码，状态码对应的文本信息，交换机名，路由键，property，消息内容
            @Override
            public void handleReturn(int i, String s, String s1, String s2, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
                String message = new String(bytes, "utf-8");
                System.out.println(i);
                System.out.println(s);
                System.out.println(s1);
                System.out.println(s2);
                System.out.println(message);
            }
        });

        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long l, boolean b) throws IOException {
                logger.info("消息投递成功，deliveryTag = " + l + ", multiple = " + b);
            }

            @Override
            public void handleNack(long l, boolean b) throws IOException {
                logger.info("消息投递失败");
                // TODO  失败后的策略
            }
        });

        // 声明路由键
        String routing_key = "love";

        // 投送的消息
        String message = "爱你呀肖川小宝贝";

        // 消息发送
        //for(int i = 0; i < 3; i++){
            // 交换机，路由键，mandatory标志位，property，消息内容
        System.out.println(System.currentTimeMillis());
            channel.basicPublish(EXCHANGE_NAME, routing_key, true,null, message.getBytes());
        //}

        //channel.close();
        //connection.close();
    }

}
