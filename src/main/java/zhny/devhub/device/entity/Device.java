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

    // 设备物理ID
    private Long devicePhysicalId;

    // 上级设备物理ID
    private Long parentDeviceId;

    private LocalDateTime offlineTime;

    // 设备是否开启
    private Boolean deviceStatus = true;

    // 设备地址
    private String deviceAddress;

    // 设备异常状态
    private String deviceState = "正常";

    private String deviceName = "未设置名称";

    // 设备分类
    private String deviceCategoryName;

    //是否绑定
    private boolean isBinding;
}
