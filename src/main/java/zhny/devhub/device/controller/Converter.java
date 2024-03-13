package zhny.devhub.device.controller;

import org.mapstruct.Mapper;
import zhny.devhub.device.entity.Device;
import zhny.devhub.device.entity.vo.DeviceVo;

@Mapper(componentModel = "spring")
public interface Converter {
    DeviceVo toDeviceVo(Device device);
}
