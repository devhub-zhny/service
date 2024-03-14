package zhny.devhub.device.service;

import zhny.devhub.device.entity.DeviceData;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author essarai
 * @since 2024-03-11 08:43:19
 */
public interface DeviceDataService extends IService<DeviceData> {

    List<DeviceData> searchByProperNameAndDeviceId(List<String> propertyNames, Long deviceID, Boolean isValid);

    void insert(DeviceData deviceData);


}
