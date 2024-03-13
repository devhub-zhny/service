package zhny.devhub.device.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Node {
    private String nodeId;
    private String deviceStatus;
    private boolean isOpen;
    private String timestamp;
    private List<Switch> switches;
    private List<Sensor> sensors;
}
