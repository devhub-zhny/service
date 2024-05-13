package zhny.devhub.device.utils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.*;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import zhny.devhub.device.entity.data.Sensor;
import zhny.devhub.device.entity.influx.SensorData;

public class InfluxDB2Example {

    public static void main(String[] args) {

        // You can generate an API token from the "API Tokens Tab" in the UI
        String token = "uke4zIwDR5IxyprFhKOxSG6HfHNQE6Im9VidOMt0WWvieDDh-mM4AzkGqaDNUSe5_1-di5Rdik_n5eQmcXnWkA==";
        String bucket = "agrox-bucket";
        String org = "agrox-org";

//        InfluxDBClient client = InfluxDBClientFactory.create("http://101.42.136.34:8086", token.toCharArray());
//        System.out.println(client.ping());

//        Point point = Point
//                .measurement("mem")
//                .addTag("host", "host1")
//                .addField("used_percent", 21.121212)
//                .time(Instant.now(), WritePrecision.NS);
//
//        WriteApiBlocking writeApi = client.getWriteApiBlocking();
//        writeApi.writePoint(bucket, org, point);

        InfluxDBClient client = InfluxDBClientFactory.create("http://101.42.136.34:8086", token.toCharArray(), org, bucket);
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        List<SensorData> list = new ArrayList<>();

        // init list
        {
            SensorData sensorData = new SensorData();
            sensorData.setTime(Instant.now());
            sensorData.setBaseId("00");
            sensorData.setLandId("01");
            sensorData.setDeviceId("01");

            sensorData.setTemperature(29.1F);
            sensorData.setPhosphorus(0.1F);
            sensorData.setPh(6.9F);
            sensorData.setHumidity(21F);
            sensorData.setConductivity(40F);
            sensorData.setNitrogen(0.9F);
            sensorData.setPotassium(0.8F);

            SensorData sensorData1 = new SensorData();
            sensorData1.setTime(Instant.now());
            sensorData1.setBaseId("00");
            sensorData1.setLandId("01");
            sensorData1.setDeviceId("02");

            sensorData1.setTemperature(29.1F);
            sensorData1.setPhosphorus(0.1F);
            sensorData1.setPh(6.9F);
            sensorData1.setHumidity(21F);
            sensorData1.setConductivity(40F);
            sensorData1.setNitrogen(0.9F);
            sensorData1.setPotassium(0.8F);
            list.add(sensorData);
            list.add(sensorData1);
        }

        // 创建Point列表
        List<Point> points = new ArrayList<>();

        // 将传感器数据列表转换为InfluxDB Point 对象
        for (SensorData s : list) {
            Point point = Point.measurement("sensor_data")
                    .addTag("baseId", s.getBaseId())
                    .addTag("landId", s.getLandId())
                    .addTag("deviceId", s.getDeviceId())
                    .addField("temperature", s.getTemperature())
                    .addField("humidity", s.getHumidity())
                    .addField("nitrogen", s.getNitrogen())
                    .addField("phosphorus", s.getPhosphorus())
                    .addField("potassium", s.getPotassium())
                    .addField("ph", s.getPh())
                    .addField("conductivity", s.getConductivity())
                    .time(Instant.now(), WritePrecision.NS);
            points.add(point);
        }

//       一次性写入所有数据
//        writeApi.writePoints(points);


        {
            //        查，使用ids那就硬拼
            QueryApi api = client.getQueryApi();
            List<String> ids = new ArrayList<>(Arrays.asList("01", "02"));

            StringBuilder queryBuilder = new StringBuilder("from(bucket:\"agrox-bucket\") ");
            queryBuilder.append("|> range(start:-2d) ")
                    .append("|> filter(fn: (r) => r[\"_measurement\"] == \"sensor_data\") ")
                    .append("|> filter(fn: (r) => ");

            for (int i = 0; i < ids.size(); i++) {
                queryBuilder.append("r[\"deviceId\"] == \"").append(ids.get(i)).append("\"");
                if (i < ids.size() - 1) {
                    queryBuilder.append(" or ");
                }
            }
            queryBuilder.append(")");
            queryBuilder.append(" |> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")");


            List<FluxTable> fluxTables = api.query(queryBuilder.toString());

            List<FluxRecord> dataList = fluxTables.get(0).getRecords();


            client.close();
        }



//        {
//            QueryApi api = client.getQueryApi();
//            List<String> ids = new ArrayList<>(Arrays.asList("01", "02"));
//            String a = "from(bucket:\"agrox-bucket\") " +
//                    "|> range(start:-2d) " +
//                    "|> filter(fn: (r) => r[\"_measurement\"] == \"sensor_data\") " +
//                    "|> filter(fn: (r) => r[\"deviceId\"] == \"01\" or r[\"deviceId\"] == \"02\") " +
//                    "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")";
//
//
//            List<FluxTable> fluxTables = api.query(a
//            );
//
//
//            System.out.println(fluxTables.get(0).getRecords().size());
//
//            client.close();
//        }

    }

}
