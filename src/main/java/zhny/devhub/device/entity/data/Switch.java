package zhny.devhub.device.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Switch {
    private Long switchId;
    private String sensorStatus;
    private boolean isOpen;
    private String timestamp;
    private Long ParentDeviceId;

}
