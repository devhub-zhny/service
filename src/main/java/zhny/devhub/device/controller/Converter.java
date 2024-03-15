package zhny.devhub.device.controller;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import zhny.devhub.device.entity.Device;
import zhny.devhub.device.entity.data.Gateway;
import zhny.devhub.device.entity.data.Node;
import zhny.devhub.device.entity.data.Sensor;
import zhny.devhub.device.entity.data.Switch;
import zhny.devhub.device.entity.vo.DeviceVo;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Converter {
    DeviceVo toDeviceVo(Device device);


    @Mapping(target = "devicePhysicalId",source = "gatewayId")
    Device gatewayToDevice(Gateway gateway);
    List<Device> gatewayListToDeviceList(List<Gateway> allGateways);


    @Mapping(target = "devicePhysicalId",source = "nodeId")
    Device nodeToDevice(Node node);
    List<Device> nodeListToDeviceList(List<Node> allNodes);


    @Mapping(target = "devicePhysicalId",source = "switchId")
    Device switchToDevice(Switch s);
    List<Device> switchListToDeviceList(List<Switch> allSwitches);

    @Mapping(target = "devicePhysicalId",source = "sensorId")
    Device sensorToDevice(Sensor sensor);
    List<Device> sensorListToDeviceList(List<Sensor> allSensors);
}
