package zhny.devhub.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import zhny.devhub.device.entity.DeviceProperty;
import zhny.devhub.device.mapper.DevicePropertyMapper;
import zhny.devhub.device.service.DevicePropertyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author essarai
 * @since 2024-03-11 08:44:22
 */
@Service
public class DevicePropertyServiceImpl extends ServiceImpl<DevicePropertyMapper, DeviceProperty> implements DevicePropertyService {


    @Override
    public List<DeviceProperty> searchByDeviceId(Long deviceId) {
        LambdaQueryWrapper<DeviceProperty> queryWrapper = Wrappers.lambdaQuery(DeviceProperty.class);
        queryWrapper.eq(deviceId != null, DeviceProperty::getDeviceId, deviceId);
        return this.baseMapper.selectList(queryWrapper);
    }


}
