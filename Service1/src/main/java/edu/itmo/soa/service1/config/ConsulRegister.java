package edu.itmo.soa.service1.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class ConsulRegister {

    private static final Logger log = LoggerFactory.getLogger(ConsulRegister.class);

    @PostConstruct
    public void registerService() {
        try {
            String serviceJson = generateBody();

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:8500/v1/agent/service/register"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(serviceJson))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Consul registration response: {}", response.body());
        } catch (Exception e) {
            log.error("Error registering service in Consul", e);
        }
    }

    private String generateBody() {
        String serviceId = "service1-client";
        String serviceName = "service1-client";
        int servicePort = 8545;
        String serviceAddress = "localhost";

        return "{"
                + "\"ID\": \"" + serviceId + "\","
                + "\"Name\": \"" + serviceName + "\","
                + "\"Address\": \"" + serviceAddress + "\","
                + "\"Port\": " + servicePort + ","
                + "\"Tags\": [\"spring\",\"client\"]"
                + "}";
    }
}
