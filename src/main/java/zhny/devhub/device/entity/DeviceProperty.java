package zhny.devhub.device.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author essarai
 * @since 2024-03-11 08:44:22
 */
@Getter
@Setter
@Builder
@Accessors(chain = true)
@TableName("device_property")
public class DeviceProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "property_id", type = IdType.ASSIGN_ID)
    private Long propertyId;

    private Long deviceId;

    private String propertyName;
}
