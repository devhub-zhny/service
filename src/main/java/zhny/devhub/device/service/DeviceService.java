package zhny.devhub.device.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import zhny.devhub.device.entity.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import zhny.devhub.device.entity.vo.SwitchVo;

import java.util.List;

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

}
