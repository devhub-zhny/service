package zhny.devhub.device.entity.influx;


import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import retrofit2.http.Field;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Measurement(name="sensor_data")
public class SensorData {

    @Column(timestamp = true)
    private Instant time;

    @Column(tag = true)
    private String baseId;

    @Column(tag = true)
    private String landId;

    @Column(tag = true)
    private String deviceId;

    @Column
    private float temperature;

    @Column
    private float humidity;

    @Column
    private float nitrogen;

    @Column
    private float phosphorus;

    @Column
    private float potassium;

    @Column
    private float ph;

    @Column
    private float conductivity;


}
