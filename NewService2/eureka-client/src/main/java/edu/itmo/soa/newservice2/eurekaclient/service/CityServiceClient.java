package edu.itmo.soa.newservice2.eurekaclient.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import edu.itmo.soa.newservice2.eurekaclient.exception.*;
import edu.itmo.soa.newservice2.eurekaclient.model.*;
import edu.itmo.soa.newservice2.eurekaclient.model.request.CitySearchRequest;
import edu.itmo.soa.newservice2.eurekaclient.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CityServiceClient {

    private final RestTemplate restTemplate;
    private final RestTemplate lbRestTemplate;
    private final boolean useEureka;
    private final String baseUrl;

    public CityServiceClient(RestTemplate restTemplate,
                             RestTemplate loadBalancedRestTemplate,
                             @Value("${service1.use-eureka:false}") boolean useEureka,
                             @Value("${service1.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.lbRestTemplate = loadBalancedRestTemplate;
        this.useEureka = useEureka;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    private RestTemplate tpl() {
        return useEureka ? lbRestTemplate : restTemplate;
    }

    private String url(String path) {
        if (useEureka) {
            if (baseUrl.startsWith("http")) return baseUrl + (path.startsWith("/") ? path.substring(1) : path);
            return "http://" + baseUrl + (path.startsWith("/") ? path : "/" + path);
        } else {
            return baseUrl + (path.startsWith("/") ? path.substring(1) : path);
        }
    }

    public City getCity(int id) {
        try {
            String u = url("cities/" + id);
            ResponseEntity<City> resp = tpl().getForEntity(u, City.class);
            if (resp.getStatusCode().is2xxSuccessful()) {
                City city = resp.getBody();
                if (city == null) throw new NotFoundException("Service1 returned empty body");
                return city;
            } else if (resp.getStatusCode().value() == 404) {
                throw new NotFoundException("Couldn't find city with id = " + id);
            } else {
                throw new ApiErrorException("Unknown response from Service1: status " + resp.getStatusCode());
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Couldn't find city with id = " + id);
        } catch (ResourceAccessException e) {
            throw new NetworkErrorException(e.getMessage());
        } catch (RestClientException e) {
            throw new ApiErrorException(e.getMessage());
        }
    }

    public City updateCity(City city) {
        try {
            CityInput input = CityMapper.toCityInput(city);


            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.registerModule(new JaxbAnnotationModule());
            xmlMapper.registerModule(new JavaTimeModule());
            xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            String xmlBody = xmlMapper.writeValueAsString(input);
            System.out.println("CityInput XML: " + xmlBody);

            String u = url("cities/" + city.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);

            HttpEntity<String> entity = new HttpEntity<>(xmlBody, headers);

            System.out.println("PUT URL: " + u);
            System.out.println("Headers: " + headers);

            ResponseEntity<City> resp =
                    tpl().exchange(u, HttpMethod.PUT, entity, City.class);

            if (resp.getStatusCode().is2xxSuccessful()) {
                City updated = resp.getBody();
                if (updated == null) throw new NotFoundException("Service1 returned an empty city");
                return updated;
            } else if (resp.getStatusCode().value() == 400) {
                throw new InvalidRequestException("Provided an invalid request!");
            } else if (resp.getStatusCode().value() == 404) {
                throw new NotFoundException("Couldn't find city with id = " + city.getId());
            } else {
                throw new ApiErrorException("Unknown response from Service1: status " + resp.getStatusCode());
            }

        } catch (HttpClientErrorException.BadRequest e) {
            System.out.println("HTTP Status: " + e.getStatusCode());
            System.out.println("Response body: " + e.getResponseBodyAsString());
            throw new InvalidRequestException("Provided an invalid request!");
        } catch (ResourceAccessException e) {
            throw new NetworkErrorException(e.getMessage());
        } catch (RestClientException e) {
            throw new ApiErrorException(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new ApiErrorException("XML serialization error: " + e.getMessage());
        }
    }


    @SuppressWarnings("unchecked")
    public List<City> getAllCities() {
        try {
            CitySearchRequest request = new CitySearchRequest();
            Pagination pagination = new Pagination(0, 1000);
            request.setPagination(pagination);
            Sort sort = new Sort("populationDensity", "ASC");
            request.setSort(sort);

            String u = url("cities/search");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);

            HttpEntity<CitySearchRequest> entity =
                    new HttpEntity<>(request, headers);

            ResponseEntity<CityPageResponse> resp =
                    tpl().exchange(u, HttpMethod.POST, entity, CityPageResponse.class);

            if (resp.getStatusCode().is2xxSuccessful()) {
                CityPageResponse page = resp.getBody();
                if (page == null) throw new ApiErrorException("Got empty cityPage");
                if (page.getCities() == null || page.getCities().isEmpty()) {
                    throw new EmptyCitiesListException("Got empty cities list");
                }
                return page.getCities();
            } else if (resp.getStatusCode().value() == 400) {
                throw new InvalidRequestException("Provided an invalid request!");
            } else {
                throw new ApiErrorException("Unknown response from Service1: status " + resp.getStatusCode());
            }
        } catch (ResourceAccessException e) {
            throw new NetworkErrorException(e.getMessage());
        } catch (RestClientException e) {
            throw new ApiErrorException(e.getMessage());
        }
    }
}

