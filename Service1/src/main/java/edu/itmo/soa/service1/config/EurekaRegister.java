package edu.itmo.soa.service1.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

@Component
public class EurekaRegister {

    private static final Logger log = Logger.getLogger("EurekaRegister");

    private static final String EUREKA_SERVER = "http://localhost:8761/eureka/apps/SERVICE1";
    private static final String SERVICE_ID = "service1";
    private static final int SERVICE_PORT = 8545;

    @PostConstruct
    public void register() {
        try {
            String body = buildBody();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(EUREKA_SERVER))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Eureka registration status=" + response.statusCode());
        } catch (Exception e) {
            log.warning("Eureka registration failed: " + e.getMessage());
        }
    }

    private String buildBody() {
        try {
            String hostAddress = "10.130.0.21";
            int wildFlyPort = 8050;

            return String.format("""
        {
          "instance": {
            "instanceId": "%s",
            "hostName": "%s",
            "app": "%s",
            "ipAddr": "%s",
            "status": "UP",
            "port": {
              "$": %d,
              "@enabled": true
            },
            "securePort": {
              "$": 0,
              "@enabled": false
            },
            "dataCenterInfo": {
              "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
              "name": "MyOwn"
            },
            "vipAddress": "%s",
            "secureVipAddress": "%s",
            "homePageUrl": "http://%s:%d/",
            "statusPageUrl": "http://%s:%d/actuator/info",
            "healthCheckUrl": "http://%s:%d/actuator/health",
            "metadata": {}
          }
        }
        """, SERVICE_ID, hostAddress, SERVICE_ID, hostAddress, wildFlyPort,
                    SERVICE_ID, SERVICE_ID,
                    hostAddress, wildFlyPort,
                    hostAddress, wildFlyPort,
                    hostAddress, wildFlyPort);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build Eureka registration body", e);
        }
    }
}
