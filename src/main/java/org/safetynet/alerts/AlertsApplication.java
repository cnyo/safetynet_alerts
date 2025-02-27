package org.safetynet.alerts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class AlertsApplication {

    @Value("${json.data.path}")
//    private static String jsonPath;

    private static final String JSON_PATH = "json/data.json";

//    @Autowired
//    private final DataLoader dataLoader;

    public static void main(String[] args) {
        SpringApplication.run(AlertsApplication.class, args);
        log.info("Try to Initializing");
//        dataLoader.init(JSON_PATH);
    }
}
