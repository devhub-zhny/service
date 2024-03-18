package zhny.devhub.device.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import zhny.devhub.device.entity.Device;
import zhny.devhub.device.entity.DeviceData;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
public class DeviceVo implements Serializable {


    private List<DeviceData> deviceDataList;

    private String devicePhysicalId;

    private Long parentDeviceId;

    private LocalDateTime offlineTime;

    // 设备是否开启
    private Boolean deviceStatus = false;

    private String deviceAddress;

    // 设备异常状态
    private String deviceState;

    private String deviceName;

    private String deviceCategoryName;
}
