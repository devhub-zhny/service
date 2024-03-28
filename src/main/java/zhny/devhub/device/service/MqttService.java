package zhny.devhub.device.service;

public interface MqttService {

    /**
     * 添加订阅主题
     *
     * @param topic 主题名称
     */
    void addTopic(String topic);

    /**
     * 取消订阅主题
     *
     * @param topic 主题名称
     */
    void removeTopic(String topic);

    /**
     * 发布主题消息内容
     *
     * @param msgContent
     * @param topic
     */
    void publish(String msgContent, String topic);

}
