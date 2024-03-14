package zhny.devhub.device.service;

import zhny.devhub.device.entity.DeviceProperty;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author essarai
 * @since 2024-03-11 08:44:22
 */
public interface DevicePropertyService extends IService<DeviceProperty> {

    List<DeviceProperty> searchByDeviceId(Long deviceId);



}
