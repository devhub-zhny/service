package zhny.devhub.device.utils;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

@Slf4j
public class MyMqttClient {

    /**
     * MQTT Broker 基本连接参数，用户名、密码为非必选参数
     */
    private String host = "ws://47.120.63.23:8083/mqtt";
    private String username = "zhny";
    private String password = "zhny";
    private String clientId = "asdfafafadsadr";
    private int timeout = 20;
    private int keepalive = 60;
    private boolean clearSession = true;

    /**
     * MQTT 客户端
     */
    private static MqttClient client;

    public static MqttClient getClient() {
        return client;
    }

    public static void setClient(MqttClient client) {
        MyMqttClient.client = client;
    }

    public MyMqttClient(String host, String username, String password, String clientId, int timeOut, int keepAlive, boolean clearSession) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.timeout = timeOut;
        this.keepalive = keepAlive;
        this.clearSession = clearSession;
    }

    /**
     * 设置 MQTT Broker 基本连接参数
     *
     * @param username
     * @param password
     * @param timeout
     * @param keepalive
     * @return
     */
    public MqttConnectOptions setMqttConnectOptions(String username, String password, int timeout, int keepalive) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setConnectionTimeout(timeout);
        options.setKeepAliveInterval(keepalive);
        options.setCleanSession(clearSession);
        options.setAutomaticReconnect(true);
        return options;
    }

    /**
     * 连接 MQTT Broker，得到 MqttClient连接对象
     */
    public void connect() throws MqttException {
        if (client == null) {
            client = new MqttClient(host, clientId, new MemoryPersistence());
            // 设置回调
            client.setCallback(new MyMqttCallback(MyMqttClient.this));
        }
        // 连接参数
        MqttConnectOptions mqttConnectOptions = setMqttConnectOptions(username, password, timeout, keepalive);
        if (!client.isConnected()) {
            client.connect(mqttConnectOptions);
        } else {
            client.disconnect();
            client.connect(mqttConnectOptions);
        }
        log.info("== MyMqttClient ==> MQTT connect success");//未发生异常，则连接成功
    }

    /**
     * 发布，默认qos为0，非持久化
     *
     * @param pushMessage
     * @param topic
     */
    public void publish(String pushMessage, String topic) {
        publish(pushMessage, topic, 0, false);
    }

    /**
     * 发布消息
     *
     * @param pushMessage
     * @param topic
     * @param qos
     * @param retained:留存
     */
    public void publish(String pushMessage, String topic, int qos, boolean retained) {
        MqttMessage message = new MqttMessage();
        message.setPayload(pushMessage.getBytes());
        message.setQos(qos);
        message.setRetained(retained);
        MqttTopic mqttTopic = MyMqttClient.getClient().getTopic(topic);
        if (null == mqttTopic) {
            log.error("== MyMqttClient ==> topic is not exist");
        }
        MqttDeliveryToken token;//Delivery:配送
        synchronized (this) {//注意：这里一定要同步，否则，在多线程publish的情况下，线程会发生死锁，分析见文章最后补充
            try {
                token = mqttTopic.publish(message);//也是发送到执行队列中，等待执行线程执行，将消息发送到消息中间件
                token.waitForCompletion(1000L);
            } catch (MqttPersistenceException e) {
                e.printStackTrace();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 订阅某个主题，qos默认为0
     *
     * @param topic
     */
    public void subscribe(String topic) {
        subscribe(topic, 0);
    }

    /**
     * 订阅某个主题
     *
     * @param topic
     * @param qos
     */
    public void subscribe(String topic, int qos) {
        try {
            MyMqttClient.getClient().subscribe(topic, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        log.info("== MyMqttClient ==> 订阅主题成功：topic = {}， qos = {}", topic, qos);
    }


    /**
     * 取消订阅主题
     *
     * @param topic 主题名称
     */
    public void cleanTopic(String topic) {
        if (client != null && client.isConnected()) {
            try {
                client.unsubscribe(topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            log.error("== MyMqttClient ==> 取消订阅失败！");
        }
        log.info("== MyMqttClient ==> 取消订阅主题成功：topic = {}", topic);
    }

}
