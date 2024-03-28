package zhny.devhub.device.service.impl;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhny.devhub.device.service.MqttService;
import zhny.devhub.device.utils.MyMqttClient;
import zhny.devhub.device.utils.MyXxxMqttMsg;

import java.util.UUID;

@Service
public class MqttServiceImpl implements MqttService {

    @Autowired
    private MyMqttClient myMqttClient;

    @Override
    public void addTopic(String topic) {
        myMqttClient.subscribe(topic);
    }

    @Override
    public void removeTopic(String topic) {
        myMqttClient.cleanTopic(topic);
    }

    @Override
    public void publish(String msgContent, String topic) {
        //MyXxxMqttMsg 转Json
        MyXxxMqttMsg myXxxMqttMsg = new MyXxxMqttMsg();
        myXxxMqttMsg.setContent(msgContent);
        myXxxMqttMsg.setTimestamp(System.currentTimeMillis());
        // TODO Md5值
        myXxxMqttMsg.setMd5(UUID.randomUUID().toString());
        String msgJson = JSON.toJSONString(myXxxMqttMsg);

        //发布消息
        myMqttClient.publish(msgJson, topic);
    }
}
