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

        if (response == null || response.getPage() == null || response.getPage().getCities() == null) {
            throw new EmptyCitiesListException("Got empty cities list");
        }

        return CityMapper.toModelList(response.getPage().getCities());
    }
}