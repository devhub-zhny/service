package zhny.devhub.device.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import zhny.devhub.device.config.MqttConfig;
import zhny.devhub.device.controller.DeviceController;
import zhny.devhub.device.entity.data.Gateway;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class MyMqttCallback implements MqttCallbackExtended {

    //手动注入
    private MqttConfig mqttConfig = SpringUtils.getBean(MqttConfig.class);


    private MyMqttClient myMqttClient;

    public MyMqttCallback(MyMqttClient myMqttClient) {
        this.myMqttClient = myMqttClient;
    }

    /**
     * MQTT Broker连接成功时被调用的方法。在该方法中可以执行 订阅系统约定的主题（推荐使用）。
     * 如果 MQTT Broker断开连接之后又重新连接成功时，主题也需要再次订阅，将重新订阅主题放在连接成功后的回调方法中比较合理。
     *
     * @param reconnect
     * @param serverURI MQTT Broker的url
     */
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        String connectMode = reconnect ? "重连" : "直连";
        log.info("== MyMqttCallback ==> MQTT 连接成功，连接方式：{}，serverURI：{}", connectMode, serverURI);
        //订阅主题

        myMqttClient.subscribe(mqttConfig.topic1, 2);
        myMqttClient.subscribe(mqttConfig.topic2, 2);
//        myMqttClient.subscribe(mqttConfig.topic3, 2);

        List<String> topicList = new ArrayList<>();
        topicList.add(mqttConfig.topic1);
        log.info("== MyMqttCallback ==> 连接方式：{}，订阅主题成功，topic：{}", connectMode, topicList);
    }


    /**
     * 丢失连接，可在这里做重连
     * 只会调用一次
     *
     * @param throwable
     */
    @Override
    public void connectionLost(Throwable throwable) {
        log.error("== MyMqttCallback ==> connectionLost 连接断开，5S之后尝试重连: {}", throwable.getMessage());
        long reconnectTimes = 1;
        while (true) {
            try {
                if (MyMqttClient.getClient().isConnected()) {
                    //判断已经重新连接成功  需要重新订阅主题 可以在这个if里面订阅主题  或者 connectComplete（方法里面）  看你们自己选择
                    log.warn("== MyMqttCallback ==> mqtt reconnect success end  重新连接  重新订阅成功");
                    return;
                }
                reconnectTimes += 1;
                log.warn("== MyMqttCallback ==> mqtt reconnect times = {} try again...  mqtt重新连接时间 {}", reconnectTimes, reconnectTimes);
                MyMqttClient.getClient().reconnect();
            } catch (MqttException e) {
                log.error("== MyMqttCallback ==> mqtt断连异常", e);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
            }
        }
    }

    /**
     * 接收到消息（subscribe订阅的主题消息）时被调用的方法
     *
     * @param topic
     * @param mqttMessage
     * @throws Exception 后得到的消息会执行到这里面
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        // TODO 对订阅信息进行操作
        /**
         * 根据订阅的主题分别处理业务。可以通过if-else或者策略模式来分别处理不同的主题消息。
         */
        Hardware hardware = new Hardware();
        // topic1主题
        if (topic.equals("sensor")) {
            log.info("== MyMqttCallback ==> messageArrived 接收消息主题: {}，接收消息内容: {}", topic, new String(mqttMessage.getPayload()));
            List<Gateway> allGateways = hardware.parseSensorData(mqttMessage.getPayload().toString());
            System.out.println(allGateways.size());
//            new DeviceController().insertGatewayDevice(allGateways);
            //TODO 业务处理


        }
        // topic2主题
        if (topic.equals("switch")) {
            log.info("== MyMqttCallback ==> messageArrived 接收消息主题: {}，接收消息内容: {}", topic, new String(mqttMessage.getPayload()));
            List<Gateway> allGateways = hardware.parseSwitchData(mqttMessage.getPayload().toString());
            System.out.println(allGateways.size());
//            new DeviceController().insertGatewayDevice(allGateways);
            //TODO 业务处理
        }
    }

    /**
     * 消息发送（publish）完成时被调用的方法
     *
     * @param iMqttDeliveryToken
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("== MyMqttCallback ==> deliveryComplete 消息发送完成，Complete= {}", iMqttDeliveryToken.isComplete());
    }

}

