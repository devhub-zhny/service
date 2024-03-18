package zhny.devhub.device.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import zhny.devhub.device.entity.Device;
import zhny.devhub.device.entity.DeviceData;
import zhny.devhub.device.entity.DeviceProperty;
import zhny.devhub.device.entity.data.*;
import zhny.devhub.device.entity.vo.DeviceVo;
import zhny.devhub.device.entity.vo.SwitchVo;
import zhny.devhub.device.service.DeviceDataService;
import zhny.devhub.device.service.DevicePropertyService;
import zhny.devhub.device.service.DeviceService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author essarai
 * @since 2024-03-11 01:29:23
 */
@Slf4j
@RestController
@RequestMapping("/v1/device")
public class DeviceController {

    @Resource
    private DeviceService deviceService;

    @Resource
    private DevicePropertyService devicePropertyService;

    @Resource
    private DeviceDataService deviceDataService;

    @Resource
    private Converter converter;

    @GetMapping("/test")
    public String test(@RequestBody String data) {
        return data;
    }

    // 创建设备
    @PostMapping("/insert")
    public void insert(@RequestBody Device device) {
        deviceService.save(device);
    }

    // 开关设备
    @PatchMapping("/switch/{id}")
    public SwitchVo open(@PathVariable Long id) {
        return deviceService.open(id);
    }

    // 删除设备
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        deviceService.removeById(id);
    }

    // 绑定设备
    @PatchMapping("/bind/{id}")
    public void bind(@PathVariable Long id) {
        deviceService.bind(id);
    }

    // 获取所有设备，并按先ID，后时间排序，ID小，时间近的在前
    @GetMapping("/all")
    public Page<Device> all(@RequestParam int current, @RequestParam int pageSize) {
        return deviceService.all(current, pageSize);
    }

    //数据存储

    @GetMapping("/data/json")
    @Transactional
    public String save(@RequestBody String data) throws Exception {
        Gson gson = new Gson();
        GatewayData gatewayData = gson.fromJson(data, GatewayData.class);

        // 获取所有网关列表
        List<Gateway> allGateways = gatewayData.getGateways();

        // 获取所有节点列表，并设置上级设备
        List<Node> allNodes = getAllNodesWithParentDeviceId(allGateways);

        // 获取所有节点的开关列表，并设置上级设备
        List<Switch> allSwitches = getAllSwitchesWithParentDeviceId(allNodes);

        // 获取所有节点的传感器列表，并设置上级设备
        List<Sensor> allSensors = getAllSensorsWithParentDeviceId(allNodes);

        // 将网关、节点、开关分为两个部分：已存在和新设备
        Map<Boolean, List<Device>> devicePartitioned = partitionDevices(allGateways, allNodes, allSwitches);

        List<Device> existingdevice = devicePartitioned.get(true);
        List<Device> newDevice = devicePartitioned.get(false);


        // 新旧数据一起搞，真的让人很烦恼
        // 新的直接插
        List<Device> allNewDevices = Stream.of(newDevice)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        deviceService.saveBatch(allNewDevices);

        // 旧的更新呀
        List<Device> allExistDevices = Stream.of(existingdevice)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (allExistDevices.size() > 0){
            List<Device> devicesToUpdate = allExistDevices.stream()
                    .map(oldDevice -> {
                        Device device = deviceService.searchByPhysicalID(oldDevice.getDevicePhysicalId());
                        device.setDeviceState(oldDevice.getDeviceState());
                        device.setDeviceStatus(oldDevice.getDeviceStatus());
                        return device;
                    })
                    .collect(Collectors.toList());
            deviceService.updateBatchById(devicesToUpdate);
        }

        // 少的怎么搞，少的先不搞


        // TODO  待优化
        // sensor & switch
        // 这个插完还得插数据这块的思路是，依据设备物理ID查询得到设备ID，之后再插入两张表
        for (Sensor sensor : allSensors) {
            Device device = converter.sensorToDevice(sensor);
            try {
                Device temp = deviceService.searchByPhysicalID(sensor.getSensorId());
                if( temp != null){
                    device.setDeviceState(sensor.getDeviceStatus());
                    device.setDeviceStatus(sensor.getIsOpen());
                    deviceService.updateById(temp);
                    device = temp;
                }else{
                    deviceService.save(device);
                }
            } catch (Exception e) {
                log.error("DeviceController.save中，插入 Device 失败");
            }

            if (devicePropertyService.searchOne(device.getDeviceId(),sensor.getSensorType()) == null){
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

        return data;
    }

    // 传感器详情包括数据
    // 获取传感器的值 依据设备ID查DeviceProperty获取属性名称，再结合二者查DeviceData
    @GetMapping("/data/{deviceId}")
    public DeviceVo getData(@PathVariable Long deviceId, @RequestParam(required = false) Boolean isValid) {
        List<DeviceProperty> deviceProperties = devicePropertyService.searchByDeviceId(deviceId);
        List<String> propertyNames = deviceProperties.stream()
                .map(DeviceProperty::getPropertyName)
                .collect(Collectors.toList());
        Device device = deviceService.getById(deviceId);
        DeviceVo deviceVo = converter.toDeviceVo(device);
        if (!propertyNames.isEmpty()) {
            // isValid为null则返回全部数据，为false则返回全部历史数据，为true则返回最新数据
            List<DeviceData> gatewayDataList = deviceDataService.searchByProperNameAndDeviceId(propertyNames, deviceId, isValid);
            deviceVo.setDeviceDataList(gatewayDataList);
        }
        return deviceVo;
    }

    //依据设备状态（在线状态）查询获取设备列表
    @GetMapping("/search")
    public Page<Device> searchByStatus(@RequestParam int status, @RequestParam int current, @RequestParam int pageSize) {
        return deviceService.searchByStatus(status, current, pageSize);
    }


    private List<Node> getAllNodesWithParentDeviceId(List<Gateway> allGateways) {
        return allGateways.stream()
                .flatMap(gateway -> gateway.getNodes().stream()
                        .peek(node -> node.setParentDeviceId(gateway.getGatewayId())))
                .collect(Collectors.toList());
    }

    private List<Sensor> getAllSensorsWithParentDeviceId(List<Node> allNodes) {
        return allNodes.stream()
                .flatMap(node -> node.getSensors().stream()
                        .peek(sensor -> sensor.setParentDeviceId(node.getNodeId())))
                .collect(Collectors.toList());
    }

    private List<Switch> getAllSwitchesWithParentDeviceId(List<Node> allNodes) {
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

        List<Long> existIdList = deviceService.list().stream()
                .map(Device::getDevicePhysicalId)
                .collect(Collectors.toList());

        boolean isEmptyExistIdList = !existIdList.isEmpty();

        List<Device> existingDevices = new ArrayList<>();
        List<Device> newDevices = new ArrayList<>();
        allDevices.forEach(device -> {
            if (isEmptyExistIdList || existIdList.contains(device.getDevicePhysicalId())) {
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
