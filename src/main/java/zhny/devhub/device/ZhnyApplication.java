package zhny.devhub.device;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("zhny.devhub.device.mapper")
public class ZhnyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhnyApplication.class, args);
    }

}
