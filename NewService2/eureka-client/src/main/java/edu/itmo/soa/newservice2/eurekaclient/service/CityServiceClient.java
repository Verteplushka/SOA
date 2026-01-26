package edu.itmo.soa.newservice2.eurekaclient.service;

import edu.itmo.soa.newservice2.eurekaclient.exception.ApiErrorException;
import edu.itmo.soa.newservice2.eurekaclient.exception.EmptyCitiesListException;
import edu.itmo.soa.newservice2.eurekaclient.exception.NotFoundException;
import edu.itmo.soa.newservice2.eurekaclient.mapper.CityMapper;
import edu.itmo.soa.newservice2.eurekaclient.model.City;
import edu.itmo.soa.newservice2.eurekaclient.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.List;

@Component
public class CityServiceClient {

    private final WebServiceTemplate webServiceTemplate;

    @Autowired
    public CityServiceClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public City getCity(int id) {
        GetCityRequest request = new GetCityRequest();
        request.setId(id);

        GetCityResponse response = (GetCityResponse) webServiceTemplate.marshalSendAndReceive(request);
        System.out.println(response);
        System.out.println(response == null);
        System.out.println(response.getCity());

        if (response == null || response.getCity() == null) {
            throw new NotFoundException("Couldn't find city with id = " + id);
        }

        return CityMapper.toModel(response.getCity());
    }

    public City updateCity(City city) {
        UpdateCityRequest request = new UpdateCityRequest();
        request.setCity(CityMapper.toClient(city));

        UpdateCityResponse response = (UpdateCityResponse) webServiceTemplate.marshalSendAndReceive(request);

        if (response == null || response.getCity() == null) {
            throw new ApiErrorException("Update failed");
        }

        return CityMapper.toModel(response.getCity());
    }

    public List<City> getAllCities() {
        GetAllCitiesRequest request = new GetAllCitiesRequest();
        CitySearchRequest search = new CitySearchRequest();
        Pagination pagination = new Pagination();
        pagination.setPage(0);
        pagination.setSize(1000);
        search.setPagination(pagination);
        Sort sort = new Sort();
        sort.setDirection("ASC");
        sort.setField("populationDensity");
        search.setSort(sort);
        request.setSearch(search);

        GetAllCitiesResponse response = (GetAllCitiesResponse) webServiceTemplate.marshalSendAndReceive(request);

        logResponseDetails(response);


        if (response == null || response.getPage() == null || response.getPage().getCities() == null) {
            throw new EmptyCitiesListException("Got empty cities list");
        }

        return CityMapper.toModelList(response.getPage().getCities());
    }

    private void logResponseDetails(GetAllCitiesResponse response) {
        if (response == null) {
            System.out.println("RESPONSE IS NULL!");
            return;
        }

        System.out.println("=== RESPONSE STRUCTURE ===");
        System.out.println("Response object: " + response);
        System.out.println("Has page: " + (response.getPage() != null));

        if (response.getPage() != null) {
            System.out.println("Page object: " + response.getPage());
            System.out.println("Has cities list: " + (response.getPage().getCities() != null));

            if (response.getPage().getCities() != null) {
                System.out.println("Cities count: " + response.getPage().getCities().size());
                System.out.println("=== CITIES DETAILS ===");

                int counter = 1;
                for (edu.itmo.soa.newservice2.eurekaclient.client.City city : response.getPage().getCities()) {
                    System.out.println("\nCity #" + counter++ + ":");
                    System.out.println("  id: " + city.getId());
                    System.out.println("  name: " + city.getName());

                    if (city.getCoordinates() != null) {
                        System.out.println("  coordinates.x: " + city.getCoordinates().getX());
                        System.out.println("  coordinates.y: " + city.getCoordinates().getY());
                    } else {
                        System.out.println("  coordinates: NULL");
                    }

                    System.out.println("  creationDate: " + city.getCreationDate());
                    System.out.println("  area: " + city.getArea());
                    System.out.println("  population: " + city.getPopulation());
                    System.out.println("  metersAboveSeaLevel: " + city.getMetersAboveSeaLevel());
                    System.out.println("  establishmentDate: " + city.getEstablishmentDate());
                    System.out.println("  populationDensity: " + city.getPopulationDensity());
                    System.out.println("  government: " + city.getGovernment());

                    if (city.getGovernor() != null) {
                        System.out.println("  governor.age: " + city.getGovernor().getAge());
                    } else {
                        System.out.println("  governor: NULL");
                    }
                }
            } else {
                System.out.println("Cities list is NULL!");
            }
        } else {
            System.out.println("Page is NULL!");
        }
        System.out.println("=== END RESPONSE STRUCTURE ===");
    }
}