package zhny.devhub.device.entity.vo;

import lombok.Data;

@Data
public class SwitchVo {

    private Long gatewayId;

    private Long nodeId;

    private Long sensorId;

    private Long switchId;

    private String deviceType;

    private boolean open;



}
