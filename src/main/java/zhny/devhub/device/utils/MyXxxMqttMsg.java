package zhny.devhub.device.utils;

import lombok.Data;

import java.io.Serializable;

@Data
public class MyXxxMqttMsg implements Serializable {

    private static final long serialVersionUID = -8303548938481407659L;

    /**
     * MD5值：MD5_lower(content + timestamp)
     */
    private String md5;

    /**
     * 消息内容
     */
    private String content = "";

    /**
     * 时间戳
     */
    private Long timestamp;


}
