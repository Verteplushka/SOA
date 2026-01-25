package edu.itmo.soa.newservice2.eurekaclient.controller;

import edu.itmo.soa.newservice2.eurekaclient.model.*;
import edu.itmo.soa.newservice2.eurekaclient.service.CityServiceClient;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Comparator;
import java.util.List;

@Endpoint
public class GenocideEndpoint {

    private static final String NS = "http://itmo.edu/soa/genocide";

    private final CityServiceClient cityServiceClient;

    public GenocideEndpoint(CityServiceClient cityServiceClient) {
        this.cityServiceClient = cityServiceClient;
    }

    @PayloadRoot(namespace = NS, localPart = "GetCityRequest")
    @ResponsePayload
    public GetCityResponse getCity(@RequestPayload GetCityRequest request) {

        City city = cityServiceClient.getCity(request.getId());

        GetCityResponse response = new GetCityResponse();
        response.setCity(mapCity(city));
        return response;
    }

    @PayloadRoot(namespace = NS, localPart = "CountPopulationRequest")
    @ResponsePayload
    public CountPopulationResponse count(@RequestPayload CountPopulationRequest request) {

        City c1 = cityServiceClient.getCity(request.getId1());
        City c2 = cityServiceClient.getCity(request.getId2());
        City c3 = cityServiceClient.getCity(request.getId3());

        CountPopulationResponse response = new CountPopulationResponse();
        response.setTotalPopulation(
                c1.getPopulation() + c2.getPopulation() + c3.getPopulation()
        );
        return response;
    }

    @PayloadRoot(namespace = NS, localPart = "MoveToPoorestRequest")
    @ResponsePayload
    public MoveToPoorestResponse move(@RequestPayload MoveToPoorestRequest request) {

        City source = cityServiceClient.getCity(request.getSourceCityId());
        List<City> cities = cityServiceClient.getAllCities();

        City poorest = cities.stream()
                .filter(c -> c.getId() != source.getId())
                .min(Comparator.comparingDouble(City::getPopulationDensity))
                .orElseThrow();

        poorest.setPopulation(poorest.getPopulation() + source.getPopulation());
        source.setPopulation(0);

        cityServiceClient.updateCity(poorest);
        cityServiceClient.updateCity(source);

        MoveToPoorestResponse response = new MoveToPoorestResponse();
        response.setSourceCity(mapCity(source));
        response.setTargetCity(mapCity(poorest));
        return response;
    }

    private City mapCity(City city) {
        City soapCity = new City();
        soapCity.setId(city.getId());
        soapCity.setName(city.getName());
        soapCity.setPopulation(city.getPopulation());
        soapCity.setPopulationDensity(city.getPopulationDensity());
        return soapCity;
    }
}

