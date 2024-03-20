package zhny.devhub.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import zhny.devhub.device.entity.DeviceData;
import zhny.devhub.device.entity.DeviceProperty;
import zhny.devhub.device.mapper.DeviceDataMapper;
import zhny.devhub.device.mapper.DevicePropertyMapper;
import zhny.devhub.device.service.DeviceDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zhny.devhub.device.service.DeviceService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author essarai
 * @since 2024-03-11 08:43:19
 */
@Service
@Slf4j
public class DeviceDataServiceImpl extends ServiceImpl<DeviceDataMapper, DeviceData> implements DeviceDataService {


    @Resource
    DevicePropertyMapper devicePropertyMapper;

    @Resource
    DeviceDataMapper deviceDataMapper;

    @Override
    public List<DeviceData> searchByProperNameAndDeviceId(List<String> propertyNames, Long deviceID, Boolean isValid) {
        LambdaQueryWrapper<DeviceData> queryWrapper = Wrappers.lambdaQuery(DeviceData.class);
        queryWrapper.eq(DeviceData::getDeviceId, deviceID)
                .in(DeviceData::getPropertyName, propertyNames)
                .orderByAsc(DeviceData::getDataTime)
                .eq(isValid != null, DeviceData::getIsValid, isValid);

        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public void insert(DeviceData deviceData) {
        LambdaQueryWrapper<DeviceProperty> deviceDataLambdaQueryWrapper = Wrappers.lambdaQuery(DeviceProperty.class)
                .eq(DeviceProperty::getDeviceId, deviceData.getDeviceId())
                .eq(DeviceProperty::getPropertyName, deviceData.getPropertyName());
        if (devicePropertyMapper.selectCount(deviceDataLambdaQueryWrapper) > 0) {
            LambdaQueryWrapper<DeviceData> q = Wrappers.lambdaQuery(DeviceData.class)
                    .eq(DeviceData::getDeviceId,deviceData.getDeviceId())
                    .eq(DeviceData::getDataTime,deviceData.getDataTime());
            if (deviceDataMapper.selectCount(q) == 0){
                this.baseMapper.insert(deviceData);
            }
        } else {
            log.info("device:-" + deviceData.getDeviceId() + "-" + deviceData.getPropertyName() + "not exist");
        }

    }
}
