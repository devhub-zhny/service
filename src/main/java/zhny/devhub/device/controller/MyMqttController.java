package zhny.devhub.device.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhny.devhub.device.service.MqttService;

@RestController
@RequestMapping("/mqtt")
@Slf4j
public class MyMqttController {
    @Autowired
    private MqttService mqttService;

    @GetMapping("/addTopic")
    public void addTopic(String topic) {
        mqttService.addTopic(topic);
    }

    @GetMapping("/removeTopic")
    public void removeTopic(String topic) {
        mqttService.removeTopic(topic);
    }

    @PostMapping("/send")
    public void sendMessage(String msgContent, String topic) {
        mqttService.publish(msgContent, topic);
    }

}

