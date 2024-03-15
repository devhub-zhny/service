package zhny.devhub.device.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gateway {
    // 物理设备ID
    private Long gatewayId;
    // 设备异常状态
    private String deviceStatus;
    // 设备开启状态
    private Boolean isOpen;
    // 上传时间
    private String timestamp;
    // 节点
    private List<Node> nodes;
}
