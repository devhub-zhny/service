package zhny.devhub.device.config;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zhny.devhub.device.utils.MyMqttClient;

@Slf4j
@Configuration
public class MqttConfig {


    public String host = "ws://47.120.63.23:8083/mqtt";
//    public String host = "ws://broker.emqx.io:8083/mqtt";

    public String username = "ac";

    public String password = "******";

    public String clientId = "skdfjhakhdfh";

    public int timeOut = 10;

    public int keepAlive = 60;

    public boolean clearSession = true;

    public String topic1 = "sensor";

    public String topic2 = "switch";

    public String topic3 = "open";

    @Bean//注入Spring
    public MyMqttClient myMqttClient() {
        MyMqttClient myMqttClient = new MyMqttClient(host, username, password, clientId, timeOut, keepAlive, clearSession);
        for (int i = 0; i < 10; i++) {
            try {
                myMqttClient.connect();
                // 这里可以订阅主题，推荐放到 MqttCallbackExtended.connectComplete方法中
                return myMqttClient;
            } catch (MqttException e) {
                log.error("== MqttConfig ==> MQTT connect exception, connect time = {}", i);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return myMqttClient;
    }

}
