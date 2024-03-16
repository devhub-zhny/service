package zhny.devhub.device.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import zhny.devhub.device.entity.DeviceData;

import javax.annotation.Resource;
import java.time.LocalDateTime;


@SpringBootTest
class DeviceDataServiceTest {
    @Resource
    DeviceDataService deviceDataService;

    @Test
    public void test(){
        DeviceData data = new DeviceData();
        data.setDeviceId(1767086431676674050l);
        data.setPropertyName("湿度");
        data.setPropertyUnit("%");
        data.setDataTime(LocalDateTime.now());
        data.setPropertyValue(12f);
        data.setAlarmThreshold(23f);
        data.setIsValid(true);

//        deviceDataService.insert(data);

    }

}