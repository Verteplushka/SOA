package edu.itmo.soa.newservice2.apigateway.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
@ComponentScan("edu.itmo.soa.newservice2.apigateway")
@Import({WebConfig.class})
public class Config {
}
