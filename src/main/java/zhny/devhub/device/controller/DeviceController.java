package zhny.devhub.device.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import zhny.devhub.device.entity.Device;
import zhny.devhub.device.service.DeviceService;

import javax.annotation.Resource;
import java.util.List;

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

    @GetMapping("/test")
    public String test(){
        return "测试成功";
    }

    // 创建设备
    @PostMapping("/insert")
    public void insert(@RequestBody Device device){
        deviceService.insert(device);
    }

    // 开关设备
    @PatchMapping("/switch/{id}")
    private void open(@PathVariable Long id){
        deviceService.open(id);
    }

    // 删除设备
    @DeleteMapping("/{id}")
    private void delete(@PathVariable Long id){
        deviceService.delete(id);
    }

    // 绑定设备
    @PatchMapping("/bind/{id}")
    private void bind(String physicsId ,@PathVariable Long id){
        deviceService.bind(physicsId,id);
    }

    // 获取所有设备
    @GetMapping("/all")
    private Page<Device> all(@RequestParam int current, @RequestParam int pageSize){
        return deviceService.all(current,pageSize);

    }

    //数据存储
    @GetMapping("/data")
    public String save(@RequestBody String data){
        return data;
    }



}
