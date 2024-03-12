package zhny.devhub.device.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
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
 * @since 2024-03-11 08:43:19
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("device_data")
public class DeviceData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "data_id", type = IdType.ASSIGN_ID)
    private Long dataId;

    private LocalDateTime dataTime;

    private Long deviceId;

    private String propertyName;

    private BigDecimal propertyValue;

    private String propertyUnit;

    private BigDecimal alarmThreshold;

    private Boolean isValid;
}
