package zhny.devhub.device.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import zhny.devhub.device.entity.Device;
import com.baomidou.mybatisplus.extension.service.IService;

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
    void open(Long id);

    Page<Device> all(int current, int pageSize);

    void bind(Long id);

    Page<Device> searchByStatus(int status,int current, int pageSize);

}
