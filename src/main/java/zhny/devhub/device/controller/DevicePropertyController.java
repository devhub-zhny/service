package zhny.devhub.device.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import zhny.devhub.device.entity.Device;
import zhny.devhub.device.entity.DeviceProperty;
import zhny.devhub.device.service.DevicePropertyService;
import zhny.devhub.device.service.DeviceService;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author essarai
 * @since 2024-03-11 01:29:23
 */
@RestController
@Slf4j
@RequestMapping("/v1")
public class DevicePropertyController {

    @Resource
    private DevicePropertyService devicePropertyService;

    @Resource
    private DeviceService deviceService;

    @Resource
    private Converter converter;

    @PostMapping("/property")
    private void insert(@RequestBody DeviceProperty deviceProperty){
        Device device = deviceService.getById(deviceProperty.getDeviceId());
        if(device == null){
            log.info("device:"+deviceProperty.getDeviceId()+"not exist");
        }
        devicePropertyService.save(deviceProperty);
    }



}
