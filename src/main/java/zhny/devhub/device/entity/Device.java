package zhny.devhub.device.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author essarai
 * @since 2024-03-11 01:29:23
 */
@Getter
@Setter
@Accessors(chain = true)
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "device_id", type = IdType.ASSIGN_ID)
    private Long deviceId;

    private String devicePhysicalId;

    private Long parentDeviceId;

    private LocalDateTime offlineTime;

    // 设备是否开启
    private Boolean deviceStatus = false;

    private String deviceAddress;

    // 设备异常状态
    private Integer deviceState;

    private String deviceName;

    private String deviceCategoryName;
}
