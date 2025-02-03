package org.safetynet.alerts;

import org.safetynet.alerts.service.JsonDataService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class AlertsApplication {

    public static void main(String[] args) throws IOException {
        JsonDataService jsonDataInit = new JsonDataService();
        jsonDataInit.init();

        SpringApplication.run(AlertsApplication.class, args);
    }
}
