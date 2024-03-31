package zhny.devhub.device.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import zhny.devhub.device.entity.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import zhny.devhub.device.entity.data.Gateway;
import zhny.devhub.device.entity.data.Node;
import zhny.devhub.device.entity.data.Sensor;
import zhny.devhub.device.entity.data.Switch;
import zhny.devhub.device.entity.vo.SwitchVo;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author essarai
 * @since 2024-03-11 01:29:23
 */
public interface DeviceService extends IService<Device> {
    SwitchVo open(Long id);

    Page<Device> all(int current, int pageSize);

    String bind(Long id, boolean isBind);


    Page<Device> searchByStatus(Boolean status,String state, Boolean bind,int current, int pageSize);

    Device searchByPhysicalID(Long physicalId);

    List<Device> searchByPhysicalIds(Long physicalIds);
    Optional<Device> searchByPhysicalIDOptional(Long physicalId);

    List<Node> getAllNodesWithParentDeviceId(List<Gateway> allGateways);

    List<Sensor> getAllSensorsWithParentDeviceId(List<Node> allNodes);

    List<Switch> getAllSwitchesWithParentDeviceId(List<Node> allNodes);

    void insertGatewayDevice(List<Gateway> allGateways);



}
