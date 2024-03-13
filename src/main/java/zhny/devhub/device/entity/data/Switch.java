package zhny.devhub.device.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Switch {
    private String switchId;
    private String sensorStatus;
    private boolean isOpen;
    private String timestamp;

}
