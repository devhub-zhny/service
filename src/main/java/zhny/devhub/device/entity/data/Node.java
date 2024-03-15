package zhny.devhub.device.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Node {
    private Long nodeId;
    // 设备异常状态
    private String deviceStatus;
    // 上级设备物理ID
    private Long parentDeviceId;
    // 设备开启状态
    private Boolean isOpen;
    private String timestamp;
    private List<Switch> switches;
    private List<Sensor> sensors;
}
