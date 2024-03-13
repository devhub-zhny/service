package zhny.devhub.device.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gateway {
    private String gatewayId;
    private String gatewayStatus;
    private boolean isOpen;
    private String timestamp;
    private List<Node> nodes;
}
