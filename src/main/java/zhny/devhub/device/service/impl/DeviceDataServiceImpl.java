package zhny.devhub.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import zhny.devhub.device.entity.DeviceData;
import zhny.devhub.device.mapper.DeviceDataMapper;
import zhny.devhub.device.service.DeviceDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zhny.devhub.device.service.DeviceService;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author essarai
 * @since 2024-03-11 08:43:19
 */
@Service
public class DeviceDataServiceImpl extends ServiceImpl<DeviceDataMapper, DeviceData> implements DeviceDataService {
    
    @Override
    public List<DeviceData> searchByProperNameAndDeviceId(List<String> propertyNames, Long deviceID, Boolean isValid) {
        LambdaQueryWrapper<DeviceData> queryWrapper = Wrappers.lambdaQuery(DeviceData.class);
        queryWrapper.eq(DeviceData::getDeviceId,deviceID)
                .in(DeviceData::getPropertyName,propertyNames)
                .eq(isValid!=null,DeviceData::getIsValid,isValid.booleanValue());

        return this.baseMapper.selectList(queryWrapper);
    }
}
