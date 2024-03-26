package zhny.devhub.device.mapper;

import zhny.devhub.device.entity.Device;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author essarai
 * @since 2024-03-11 01:29:23
 */
@Mapper
public interface DeviceMapper extends BaseMapper<Device> {

    List<Device> searchByPhysicalIds(Long physicalIds);
}
