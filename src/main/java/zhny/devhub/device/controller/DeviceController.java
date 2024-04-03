package zhny.devhub.device.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import zhny.devhub.device.service.MqttService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private MqttService mqttService;


    @GetMapping("/test")
    public String test(@RequestBody String data) {
        return data;
    }


    // 创建设备
    @PostMapping("/insert")
    public void insert(@RequestBody Device device) {
        deviceService.saveOrUpdate(device);
    }

    // 修改设备名
    @PatchMapping("/updateName/{id}")
    public String updateName(@PathVariable Long id,@RequestParam String name){
        Device device = deviceService.getById(id);
        if(device == null){
            return "设备不存在";
        }
        device.setDeviceName(name);
        deviceService.updateById(device);
        return "修改成功";
    }


    // 开关设备
    @PatchMapping("/switch/{id}")
    public SwitchVo open(@PathVariable Long id) {
        SwitchVo vo = deviceService.open(id);
        Long physicalId = vo.getIds().get(vo.getIds().size()-1);
        String res = "%"+Long.toHexString(physicalId)+"0";
        if (vo.isState()){
            res = res+"1";
        }else {
            res = res+"0";
        }
        // 向MQTT服务器发送指令
        mqttService.publish(res,"data");
//        mqttService.publish("%0101011101","data");
        return vo;
    }

    // 删除设备
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        try {
            deviceService.removeById(id);
            log.info("删除成功");
            return "删除成功";
        } catch (Exception e) {
            log.error(id + "设备不存在");
            return "设备不存在";
        }

    }


    // 绑定设备
    @PatchMapping("/bind/{id}")
    public String bind(@PathVariable Long id, @RequestParam boolean isBind) {
        return deviceService.bind(id, isBind);
    }


    // 获取所有设备，并按先ID，后时间排序，ID小，时间近的在前
    @GetMapping("/all")
    public Page<Device> all(@RequestParam int current, @RequestParam int pageSize) {
        return deviceService.all(current, pageSize);
    }


    //数据存储,传入JSON直接
    @GetMapping("/data/json")
    @Transactional
    public String save(@RequestBody String data) throws Exception {
        Gson gson = new Gson();
        GatewayData gatewayData = gson.fromJson(data, GatewayData.class);

        // 获取所有网关列表
        List<Gateway> allGateways = gatewayData.getGateways();

        deviceService.insertGatewayDevice(allGateways);
        return data;
    }


    // 传感器详情包括数据
    // 获取传感器的值 依据设备ID查DeviceProperty获取属性名称，再结合二者查DeviceData
    @GetMapping("/data/{deviceId}")
    public DeviceVo getData(@PathVariable Long deviceId, @RequestParam(required = false) Boolean isValid) {
        Device device = deviceService.getById(deviceId);
        if (!device.getIsBinding()) {
            DeviceVo vo = new DeviceVo();
            vo.setDeviceName("该设备未绑定无法查看数据,请先绑定");
            return vo;
        }
        List<DeviceProperty> deviceProperties = devicePropertyService.searchByDeviceId(deviceId);
        List<String> propertyNames = deviceProperties.stream()
                .map(DeviceProperty::getPropertyName)
                .collect(Collectors.toList());

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
    public Page<Device> searchByStatus(@RequestParam(required = false) Boolean status,
                                       @RequestParam(required = false) String state,
                                       @RequestParam(required = false) Boolean bind,
                                       @RequestParam int current,
                                       @RequestParam int pageSize) {
        return deviceService.searchByStatus(status, state, bind, current, pageSize);
    }
}
