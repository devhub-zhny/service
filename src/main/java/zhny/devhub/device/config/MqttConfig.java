package zhny.devhub.device.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.UUID;

/**
 * mqtt连接配置
 */
@Configuration
public class MqttConfig {

    /**
     * 创建连接
     *
     * @return
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        // mqtt用户名&密码
        String userName = "";
        String pwd = "";
        // mqtt服务地址，可以是多个
//        options.setServerURIs(new String[]{"tcp://broker.emqx.io:1883"});
        options.setServerURIs(new String[]{"ws://broker.emqx.io:8083/mqtt"});
        options.setUserName(userName);
        options.setPassword(pwd.toCharArray());
        factory.setConnectionOptions(options);

        return factory;
    }

    /**
     * 2、接收消息的通道
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /**
     * 接收消息
     *
     * @return
     */
    @Bean
    public MessageProducer inbound() {
        // 订阅主题,保证唯一性
        String inClientId = UUID.randomUUID().toString().replaceAll("a", "");
        // 最后的#相当于通配符的概念
        String[] topic = {"zhny"};
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                inClientId,
                mqttClientFactory(),
                topic);

        adapter.setCompletionTimeout(5000);

        DefaultPahoMessageConverter defaultPahoMessageConverter = new DefaultPahoMessageConverter();
        // 按字节接收消息
//        defaultPahoMessageConverter.setPayloadAsBytes(true);
        adapter.setConverter(defaultPahoMessageConverter);
        // 设置QoS
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());

        return adapter;
    }


    /**
     * 3、消息处理
     * ServiceActivator注解表明：当前方法用于处理MQTT消息，inputChannel参数指定了用于消费消息的channel
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> {
            String payload = message.getPayload().toString();

            // byte[] bytes = (byte[]) message.getPayload(); // 收到的消息是字节格式
            String topic = message.getHeaders().get("mqtt_receivedTopic").toString();

            // 可以根据topic进行处理不同的业务类型
            System.out.println("主题[" + topic + "]，负载：" + payload);
        };
    }


    /**
     * 发送消息的通道
     *
     * @return
     */
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * 发送消息
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler outbound() {

        // 连接clientId保证唯一
        String outClientId = "mqtt-test";

        // 发送消息和消费消息Channel可以使用相同MqttPahoClientFactory
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(outClientId, mqttClientFactory());
        // 如果设置成true，即异步，发送消息时将不会阻塞。
        messageHandler.setAsync(true);
        // 设置默认的topic
        // messageHandler.setDefaultTopic("defaultTopic");
        // 设置默认QoS
        messageHandler.setDefaultQos(1);

        // Paho消息转换器
        DefaultPahoMessageConverter defaultPahoMessageConverter = new DefaultPahoMessageConverter();

        // 发送默认按字节类型发送消息
        // defaultPahoMessageConverter.setPayloadAsBytes(true);
        messageHandler.setConverter(defaultPahoMessageConverter);
        return messageHandler;
    }
}