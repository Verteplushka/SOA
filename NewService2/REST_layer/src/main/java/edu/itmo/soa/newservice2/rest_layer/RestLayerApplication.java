package edu.itmo.soa.newservice2.rest_layer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class RestLayerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestLayerApplication.class, args);
	}

}
