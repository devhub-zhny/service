package zhny.devhub.device.controller;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import zhny.devhub.device.entity.Device;
import zhny.devhub.device.entity.DeviceData;
import zhny.devhub.device.entity.DeviceProperty;
import zhny.devhub.device.entity.data.Gateway;
import zhny.devhub.device.entity.data.Node;
import zhny.devhub.device.entity.data.Sensor;
import zhny.devhub.device.entity.data.Switch;
import zhny.devhub.device.entity.vo.DeviceVo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface Converter {
    DeviceVo toDeviceVo(Device device);


    @Mapping(target = "devicePhysicalId",source = "gatewayId")
    @Mapping(target = "deviceState",source = "deviceStatus")
    @Mapping(target = "deviceStatus",source = "isOpen")
    @Mapping(target = "offlineTime",source = "timestamp")
    Device gatewayToDevice(Gateway gateway);
    List<Device> gatewayListToDeviceList(List<Gateway> allGateways);


    @Mapping(target = "devicePhysicalId",source = "nodeId")
    @Mapping(target = "deviceState",source = "deviceStatus")
    @Mapping(target = "deviceStatus",source = "isOpen")
    @Mapping(target = "offlineTime",source = "timestamp")
    Device nodeToDevice(Node node);
    List<Device> nodeListToDeviceList(List<Node> allNodes);


    @Mapping(target = "devicePhysicalId",source = "switchId")
    @Mapping(target = "deviceState",source = "deviceStatus")
    @Mapping(target = "deviceStatus",source = "isOpen")
    @Mapping(target = "offlineTime",source = "timestamp")
    Device switchToDevice(Switch s);
    List<Device> switchListToDeviceList(List<Switch> allSwitches);

    @Mapping(target = "devicePhysicalId",source = "sensorId")
    @Mapping(target = "deviceState",source = "deviceStatus")
    @Mapping(target = "deviceStatus",source = "isOpen")
    @Mapping(target = "offlineTime",source = "timestamp")
    Device sensorToDevice(Sensor sensor);
    List<Device> sensorListToDeviceList(List<Sensor> allSensors);

    default LocalDateTime dateMap(String strDateTime) {
        return LocalDateTime.parse(strDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

//    @Mapping(target = "deviceStatus",source = "isOpen")
//    DeviceData map(Sensor sensor);
//    List<DeviceData> sensorListToDataList(List<Sensor> allSensors);
//
//    @Mapping(target = "deviceStatus",source = "isOpen")
//    DeviceProperty maps(Sensor sensor);
//    List<DeviceProperty> sensorListToPropertyList(List<Sensor> allSensors);
}
