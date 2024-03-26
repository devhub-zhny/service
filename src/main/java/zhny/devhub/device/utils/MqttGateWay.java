package zhny.devhub.device.utils;


import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * 定义消息发送的接口
 */
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateWay {

    /**
     * 发送消息
     *
     * @param payload 发送的消息
     */
    void sendToMqtt(String payload);

    /**
     * 指定topic消息发送
     *
     * @param topic   指定topic
     * @param payload 消息
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);

    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String payload);

    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, byte[] payload);
}