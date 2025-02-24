package org.safetynet.alerts;

import org.safetynet.alerts.service.JsonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlertsApplication {

    @Value("${json.data.path}")
    private static String jsonPath;

    @Autowired
    private static JsonDataService jsonDataService;

    public static void main(String[] args) {
        SpringApplication.run(AlertsApplication.class, args);
        jsonDataService.init(jsonPath);
    }
}
