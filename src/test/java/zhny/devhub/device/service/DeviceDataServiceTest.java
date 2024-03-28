package zhny.devhub.device.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import zhny.devhub.device.entity.DeviceData;
import zhny.devhub.device.entity.data.Gateway;
import zhny.devhub.device.utils.Hardware;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


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

    @Test
    public void testH(){
        Hardware hardware = new Hardware();
        List<Gateway> gatewayList = hardware.parseSensorData(null);
        List<Gateway> switchList = hardware.parseSwitchData(null);
        System.out.println(gatewayList.size());
        System.out.println(switchList.size());

    }




}