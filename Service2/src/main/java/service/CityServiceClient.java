package service;

import model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@ApplicationScoped
public class CityServiceClient {

    private final String BASE_URL = "http://localhost:8080/Service1/cities"; // URL первого сервиса (Spring Jetty)
    private final Client client;

    public CityServiceClient() {
        this.client = ClientBuilder.newBuilder()
                .hostnameVerifier((h, s) -> true) // для самоподписанного сертификата
                .build();
    }

    public City getCity(int id) {
        try {
            return client.target(BASE_URL + "/" + id)
                    .request(MediaType.APPLICATION_XML)
                    .get(City.class);
        } catch (Exception e) {
           return null;
        }
    }
    public List<City> getAllCities() {
        try {
            CitySearchRequest request = new CitySearchRequest();
            request.setPagination(new Pagination(0, 1000)); // берём сразу всех
            request.setSort(new Sort("populationDensity", "ASC"));

            cityPageResponse response = client.target(BASE_URL + "/search")
                    .request(MediaType.APPLICATION_XML)
                    .post(Entity.entity(request, MediaType.APPLICATION_XML), cityPageResponse.class);

            return response.getCities();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public CityInput toCityInput(City city) {
        CityInput input = new CityInput();
        input.setName(city.getName());
        if (city.getCoordinates() != null) {
            CoordinatesInput coords = new CoordinatesInput();
            coords.setX(city.getCoordinates().getX());
            coords.setY(city.getCoordinates().getY());
            input.setCoordinates(coords);
        }
        input.setArea(city.getArea());
        input.setPopulation(city.getPopulation());
        input.setMetersAboveSeaLevel(city.getMetersAboveSeaLevel());
        input.setEstablishmentDate(city.getEstablishmentDate());
        input.setPopulationDensity(city.getPopulationDensity());
        input.setGovernment(city.getGovernment());
        if (city.getGovernor() != null) {
            GovernorInput gov = new GovernorInput();
            gov.setAge(city.getGovernor().getAge());
            input.setGovernor(gov);
        }
        return input;
    }

    public void updateCity(City city) {
        CityInput cityInput = toCityInput(city);

        client.target(BASE_URL + "/" + city.getId())
                .request(MediaType.APPLICATION_XML)
                .put(Entity.entity(cityInput, MediaType.APPLICATION_XML));
    }
}
