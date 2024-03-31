package zhny.devhub.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import zhny.devhub.device.controller.Converter;
import zhny.devhub.device.entity.Device;
import zhny.devhub.device.entity.DeviceData;
import zhny.devhub.device.entity.DeviceProperty;
import zhny.devhub.device.entity.data.Gateway;
import zhny.devhub.device.entity.data.Node;
import zhny.devhub.device.entity.data.Sensor;
import zhny.devhub.device.entity.data.Switch;
import zhny.devhub.device.entity.vo.SwitchVo;
import zhny.devhub.device.mapper.DeviceMapper;
import zhny.devhub.device.service.DeviceDataService;
import zhny.devhub.device.service.DevicePropertyService;
import zhny.devhub.device.service.DeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zhny.devhub.device.service.MqttService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Resource
    private DevicePropertyService devicePropertyService;

    @Resource
    private DeviceDataService deviceDataService;

    @Resource
    private Converter converter;


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
    public String bind(Long id, boolean isBind) {
        Device device = this.baseMapper.selectById(id);
        if (device != null) {
            device.setIsBinding(isBind);
            this.baseMapper.updateById(device);
            log.info(device.getDevicePhysicalId() + "设备绑定成功");
            if (isBind){
                return "物理设备编号"+device.getDevicePhysicalId() + "设备绑定成功";
            }
            return "物理设备编号"+device.getDevicePhysicalId() + "设备解绑成功";

        } else {
            log.info(id + "设备不存在");
            return "设备编号"+id + "设备不存在";
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

    @Override
    public List<Device> searchByPhysicalIds(Long physicalIds) {
        return this.baseMapper.searchByPhysicalIds(physicalIds);
    }

    @Override
    public void insertGatewayDevice(List<Gateway> allGateways){

        // 获取所有节点列表，并设置上级设备
        List<Node> allNodes = getAllNodesWithParentDeviceId(allGateways);

        // 获取所有节点的开关列表，并设置上级设备
        List<Switch> allSwitches = getAllSwitchesWithParentDeviceId(allNodes);

        // 获取所有节点的传感器列表，并设置上级设备
        List<Sensor> allSensors = getAllSensorsWithParentDeviceId(allNodes);

        // 将网关、节点、开关分为两个部分：已存在和新设备
        Map<Boolean, List<Device>> devicePartitioned = partitionDevices(allGateways, allNodes, allSwitches);

        List<Device> existingDevice = devicePartitioned.get(true);
        List<Device> newDevice = devicePartitioned.get(false);


        // 新旧数据一起搞，真的让人很烦恼
        // 新的直接插
        List<Device> allNewDevices = Stream.of(newDevice)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        saveBatch(allNewDevices);

        // 旧的更新呀
        List<Device> allExistDevices = Stream.of(existingDevice)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (allExistDevices.size() > 0) {
            List<Device> devicesToUpdate = allExistDevices.stream()
                    .map(oldDeviceNewData -> {
                        Device device = searchByPhysicalID(oldDeviceNewData.getDevicePhysicalId());
                        device.setDeviceState(oldDeviceNewData.getDeviceState());
                        device.setDeviceStatus(oldDeviceNewData.getDeviceStatus());
                        device.setOfflineTime(oldDeviceNewData.getOfflineTime());
                        return device;
                    })
                    .collect(Collectors.toList());
            updateBatchById(devicesToUpdate);
        }

        // 少的怎么搞，少的先不搞


        // TODO  待优化
        // sensor & switch
        // 这个插完还得插数据这块的思路是，依据设备物理ID查询得到设备ID，之后再插入两张表
        for (Sensor sensor : allSensors) {
            Device device = converter.sensorToDevice(sensor);
            try {
                Device temp = searchByPhysicalID(sensor.getSensorId());
                if (temp != null) {
                    device.setDeviceState(sensor.getDeviceStatus());
                    device.setDeviceStatus(sensor.getIsOpen());
                    updateById(temp);
                    device = temp;
                } else {
                    save(device);
                }
            } catch (Exception e) {
                log.error("DeviceController.save中，插入 Device 失败");
            }

            if (devicePropertyService.searchOne(device.getDeviceId(), sensor.getSensorType()) == null) {
                DeviceProperty deviceProperty = DeviceProperty.builder()
                        .deviceId(device.getDeviceId())
                        .propertyName(sensor.getSensorType())
                        .build();
                try {
                    devicePropertyService.save(deviceProperty);
                } catch (Exception e) {
                    log.error("DeviceController.save中，插入 DeviceProperty 失败");
                }
            }
            DeviceData deviceData = converter.sensorToDeviceData(sensor);
            deviceData.setDeviceId(device.getDeviceId());
            deviceDataService.insert(deviceData);
        }

    }

    // 根据物理ID查询设备，返回Optional对象
    public Optional<Device> searchByPhysicalIDOptional(Long physicalId) {
        if (physicalId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(searchByPhysicalID(physicalId));
    }

    public List<Node> getAllNodesWithParentDeviceId(List<Gateway> allGateways) {
        return allGateways.stream()
                .flatMap(gateway -> gateway.getNodes().stream()
                        .peek(node -> node.setParentDeviceId(gateway.getGatewayId())))
                .collect(Collectors.toList());
    }

    public List<Sensor> getAllSensorsWithParentDeviceId(List<Node> allNodes) {
        return allNodes.stream()
                .flatMap(node -> node.getSensors().stream()
                        .peek(sensor -> sensor.setParentDeviceId(node.getNodeId())))
                .collect(Collectors.toList());
    }

    public List<Switch> getAllSwitchesWithParentDeviceId(List<Node> allNodes) {
        return allNodes.stream()
                .flatMap(node -> node.getSwitches().stream()
                        .peek(aSwitch -> aSwitch.setParentDeviceId(node.getNodeId())))
                .collect(Collectors.toList());
    }

    // 按数据库是否存在分区
    private Map<Boolean, List<Device>> partitionDevices(List<Gateway> allGateways, List<Node> allNodes, List<Switch> allSwitches) {
        List<Device> allDevices = new ArrayList<>();
        allDevices.addAll(converter.gatewayListToDeviceList(allGateways));
        allDevices.addAll(converter.nodeListToDeviceList(allNodes));
        allDevices.addAll(converter.switchListToDeviceList(allSwitches));

        List<Long> existIdList = list().stream()
                .map(Device::getDevicePhysicalId)
                .collect(Collectors.toList());

        boolean isEmptyExistIdList = existIdList.isEmpty();

        List<Device> existingDevices = new ArrayList<>();
        List<Device> newDevices = new ArrayList<>();
        allDevices.forEach(device -> {
            if (!isEmptyExistIdList && existIdList.contains(device.getDevicePhysicalId())) {
                existingDevices.add(device);
            } else {
                newDevices.add(device);
            }
        });

        Map<Boolean, List<Device>> partitionedDevices = new HashMap<>();
        partitionedDevices.put(true, existingDevices);
        partitionedDevices.put(false, newDevices);
        return partitionedDevices;
    }



}
