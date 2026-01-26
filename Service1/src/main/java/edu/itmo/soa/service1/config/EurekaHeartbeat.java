package edu.itmo.soa.service1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class EurekaHeartbeat {

    private static final Logger LOG = LoggerFactory.getLogger(EurekaHeartbeat.class);

    private static final String EUREKA_URL = "http://localhost:8761/eureka/apps/SERVICE1/service1";

    private final HttpClient client = HttpClient.newHttpClient();

    @Scheduled(fixedRate = 30_000)
    public void heartbeat() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(EUREKA_URL))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() == 200) {
                LOG.info("Eureka heartbeat successful");
            } else {
                LOG.warn("Eureka heartbeat failed: HTTP " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            LOG.error("Eureka heartbeat exception", e);
        }
    }
}
