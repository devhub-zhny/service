package zhny.devhub.device.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
    // 物理设备ID
    private Long sensorId;
    // 设备异常状态
    private String sensorStatus;
    private boolean isOpen;
    private String sensorType;
    private double value;
    private String unit;
    private String timestamp;
    private Long ParentDeviceId;
}