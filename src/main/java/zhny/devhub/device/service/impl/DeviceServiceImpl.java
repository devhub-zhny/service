package zhny.devhub.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import zhny.devhub.device.entity.Device;
import zhny.devhub.device.entity.DeviceProperty;
import zhny.devhub.device.entity.vo.SwitchVo;
import zhny.devhub.device.mapper.DeviceMapper;
import zhny.devhub.device.service.DeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author essarai
 * @since 2024-03-11 01:29:23
 */
@Slf4j
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    @Override
    public SwitchVo open(Long id) {
        Device device = this.baseMapper.selectById(id);
        SwitchVo switchVo = new SwitchVo();
        if (device == null) {
            log.info(id + "设备不存在");
            switchVo.setMessage("设备不存在");
            return switchVo;
        }

        Boolean bind = device.getIsBinding();
        if (!bind) {
            log.info(id + "设备还没绑定");
            switchVo.setMessage("设备还没绑定");
            return switchVo;
        }

        // 发送物理指令
        List<Long> ids = new ArrayList<>();
        Optional<Device> tempOptional = Optional.of(device);
        while (tempOptional.isPresent()) {
            ids.add(0, tempOptional.get().getDevicePhysicalId());
            tempOptional = searchByPhysicalIDOptional(tempOptional.get().getParentDeviceId());
        }

        // 更新数据库
        device.setDeviceStatus(!device.getDeviceStatus());
        this.baseMapper.updateById(device);
        switchVo.setMessage("操作成功");
        log.info(id + "开关操作成功");

        // 构造返回对象
        switchVo.setOpen(!device.getDeviceStatus());
        switchVo.setDeviceType(device.getDeviceCategoryName());
        switchVo.setIds(ids);

        return switchVo;
    }

    @Override
    public Page<Device> all(int current, int pageSize) {
        return this.baseMapper.selectPage(
                new Page<>(current, pageSize),
                new QueryWrapper<Device>()
                        .orderByAsc("device_physical_id")
                        .orderByDesc("offline_time"));
    }

    @Override
    public void bind(Long id) {
        Device device = this.baseMapper.selectById(id);
        if (device != null) {
            device.setIsBinding(true);
            this.baseMapper.updateById(device);
            log.info(device.getDeviceName() + "设备绑定成功");
        } else {
            log.info(id + "设备不存在");
        }

    }

    @Override
    public Page<Device> searchByStatus(Boolean status, String state, Boolean bind, int current, int pageSize) {
        Page<Device> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Device> queryWrapper = Wrappers.lambdaQuery(Device.class)
                .eq(status != null, Device::getDeviceStatus, status)
                .eq(bind != null,Device::getIsBinding,bind)
                .eq(state != null, Device::getDeviceState,state)
                .orderByAsc(Device::getDevicePhysicalId)
                .orderByDesc(Device::getOfflineTime);
        return this.baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Device searchByPhysicalID(Long physicalId){
        Device device = this.baseMapper.selectOne(
                Wrappers.lambdaQuery(Device.class)
                        .eq(Device::getDevicePhysicalId,physicalId)
        );
        return device;
    }

    // 根据物理ID查询设备，返回Optional对象
    private Optional<Device> searchByPhysicalIDOptional(Long physicalId) {
        if (physicalId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(searchByPhysicalID(physicalId));
    }


}
