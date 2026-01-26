package edu.itmo.soa.service1.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ConsulRegister {

    private static final Logger log = LoggerFactory.getLogger(ConsulRegister.class);

    private final HttpClient client = HttpClient.newHttpClient();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private String serviceId = "service1";
    private String checkId = "service:" + serviceId;
    private volatile boolean isRegistered = false;

    @PostConstruct
    public void registerService() {
        try {
            String serviceJson = generateRegistrationBody();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:8500/v1/agent/service/register"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(serviceJson))
                    .timeout(Duration.ofSeconds(5))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                log.info("Service successfully registered in Consul");
                isRegistered = true;
                startHeartbeat();
            } else {
                log.error("Consul registration failed. Status: {}, Body: {}",
                        response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("Error registering service in Consul", e);
        }
    }

    private String generateRegistrationBody() {
        String serviceId = "service1";
        String serviceName = "service1";
        int servicePort = 8545;
        String serviceAddress = "localhost";
        int ttlSeconds = 60;
        int deregisterTimeout = 120;

        return "{"
                + "\"ID\": \"" + serviceId + "\","
                + "\"Name\": \"" + serviceName + "\","
                + "\"Address\": \"" + serviceAddress + "\","
                + "\"Port\": " + servicePort + ","
                + "\"Tags\": [\"spring\", \"client\"],"
                + "\"Check\": {"
                + "  \"CheckID\": \"" + checkId + "\","
                + "  \"Name\": \"Service TTL Check\","
                + "  \"TTL\": \"" + ttlSeconds + "s\","
                + "  \"DeregisterCriticalServiceAfter\": \"" + deregisterTimeout + "s\","
                + "  \"Status\": \"passing\""
                + "}"
                + "}";
    }

    private void startHeartbeat() {
        int heartbeatInterval = 30;
        scheduler.scheduleAtFixedRate(() -> {
            if (!isRegistered) {
                return;
            }
            try {
                sendHeartbeat();
            } catch (Exception e) {
                log.warn("Heartbeat failed: {}", e.getMessage());
            }
        }, 0, heartbeatInterval, TimeUnit.SECONDS);

        log.info("Heartbeat scheduler started with interval {}s", heartbeatInterval);
    }

    private void sendHeartbeat() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:8500/v1/agent/check/pass/" + checkId))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(3))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                log.debug("Heartbeat sent successfully");
            } else {
                log.warn("Heartbeat failed with status: {}", response.statusCode());
            }
        } catch (Exception e) {
            log.error("Error sending heartbeat to Consul", e);
        }
    }
}