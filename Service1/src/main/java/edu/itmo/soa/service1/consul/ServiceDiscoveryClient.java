package edu.itmo.soa.service1.consul;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceDiscoveryClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public String getService1ProviderUrl() {
        String url = "http://localhost:8500/v1/catalog/service/service1";
        ServiceInfo[] services = restTemplate.getForObject(url, ServiceInfo[].class);
        if (services != null && services.length > 0) {
            return "http-remoting://" + services[0].getServiceAddress() + ":" + services[0].getServicePort();
        }
        throw new RuntimeException("Service1 not found in Consul");
    }

    public static class ServiceInfo {
        private String ServiceAddress;
        private int ServicePort;

        public String getServiceAddress() { return ServiceAddress; }
        public int getServicePort() { return ServicePort; }
        public void setServiceAddress(String serviceAddress) { ServiceAddress = serviceAddress; }
        public void setServicePort(int servicePort) { ServicePort = servicePort; }
    }
}

