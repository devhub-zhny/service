package zhny.devhub.device.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhny.devhub.device.utils.MqttGateWay;

/**
 * 对外暴露发送消息的controller
 */
@RestController
@RequestMapping("/mqtt")
public class MqttController {

    @Autowired
    private MqttGateWay mqttGateWay;

    @PostMapping("/send")
    public String sendMessage(String topic, String message) {
        // 发送消息到指定topic
        mqttGateWay.sendToMqtt(topic, 1, message);
        return "send topic: " + topic + ", message : " + message;
    }
}