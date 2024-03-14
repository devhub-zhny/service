package zhny.devhub.device.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.*;
import zhny.devhub.device.entity.Device;
import zhny.devhub.device.entity.DeviceData;
import zhny.devhub.device.entity.DeviceProperty;
import zhny.devhub.device.entity.data.GatewayData;
import zhny.devhub.device.entity.vo.DeviceVo;
import zhny.devhub.device.service.DeviceDataService;
import zhny.devhub.device.service.DevicePropertyService;
import zhny.devhub.device.service.DeviceService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author essarai
 * @since 2024-03-11 01:29:23
 */
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
    public String test(@RequestBody String data){
        return data;
    }

    // 创建设备
    @PostMapping("/insert")
    public void insert(@RequestBody Device device){
        deviceService.save(device);
    }

    // 开关设备
    @PatchMapping("/switch/{id}")
    private void open(@PathVariable Long id){
        deviceService.open(id);
    }

    // 删除设备
    @DeleteMapping("/{id}")
    private void delete(@PathVariable Long id){
        deviceService.removeById(id);
    }

    // 绑定设备
    @PatchMapping("/bind/{id}")
    private void bind(@PathVariable Long id){
        deviceService.bind(id);
    }

    // 获取所有设备
    @GetMapping("/all")
    private Page<Device> all(@RequestParam int current, @RequestParam int pageSize){
        return deviceService.all(current,pageSize);
    }

    //数据存储
    @GetMapping("/data")
    public String save(@RequestBody String data) throws Exception{
        Gson gson = new Gson();
        GatewayData gatewayData = gson.fromJson(data, GatewayData.class);
        // TODO 数据存入数据库中
        return  data;
    }

    // 传感器详情包括数据
    // 获取传感器的值 依据设备ID查DeviceProperty获取属性名称，再结合二者查DeviceData
    @GetMapping("/data/{deviceId}")
    public DeviceVo getData(@PathVariable Long deviceId,@RequestParam(required = false) Boolean isValid){
        List<DeviceProperty> deviceProperties = devicePropertyService.searchByDeviceId(deviceId);
        List<String> propertyNames = deviceProperties.stream()
                .map(DeviceProperty::getPropertyName)
                .collect(Collectors.toList());
        Device device = deviceService.getById(deviceId);
        DeviceVo deviceVo = converter.toDeviceVo(device);
        if (!propertyNames.isEmpty()){
            // isValid为null则返回全部数据，为false则返回全部历史数据，为true则返回最新数据
            List<DeviceData> gatewayDataList = deviceDataService.searchByProperNameAndDeviceId(propertyNames,deviceId,isValid);
            deviceVo.setDeviceDataList(gatewayDataList);
        }
        return deviceVo;
    }

    //依据设备状态查询获取设备列表
    @GetMapping("/search")
    private Page<Device> searchByStatus(int status, @RequestParam int current, @RequestParam int pageSize){
        return deviceService.searchByStatus(status,current,pageSize);
    }


}
