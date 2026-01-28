package edu.itmo.soa.service1;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.jboss.logging.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Singleton
@Startup
public class ConsulRegister {
    private static final Logger log = Logger.getLogger(ConsulRegister.class);

    @PostConstruct
    private void registerService() {
        try {
            String serviceJson = generateBody();

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:8500/v1/agent/service/register"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(serviceJson))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Consul registration response: " + response.body());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static String generateBody(){
        String serviceId = "service1-ejb";
        String serviceName = "service1-ejb";
        int servicePort = 8080;
        String serviceAddress = "localhost";

        return "{"
                + "\"ID\": \"" + serviceId + "\","
                + "\"Name\": \"" + serviceName + "\","
                + "\"Address\": \"" + serviceAddress + "\","
                + "\"Port\": " + servicePort + ","
                + "\"Tags\": [\"ejb\",\"remote\"]"
                + "}";
    }
}
