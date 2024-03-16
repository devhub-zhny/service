package zhny.devhub.device.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class SwitchVo {

    private List<Long> Ids;

    private String deviceType;

    private boolean open;



}
