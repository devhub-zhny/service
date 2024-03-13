package zhny.devhub.device.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
    private String sensorId;
    private String sensorStatus;
    private boolean isOpen;
    private String sensorType;
    private double value;
    private String unit;
    private String timestamp;
}