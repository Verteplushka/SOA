package service;

import exception.*;
import jakarta.enterprise.context.ApplicationScoped;
import model.*;
import model.request.CitySearchRequest;
import model.CityPageResponse;
import retrofit2.Response;
import retrofit2.converter.jaxb.JaxbConverterFactory;
import retrofit2.Retrofit;
import util.CityMapper;

import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class CityServiceClient {

    private final String BASE_URL = "https://158.160.204.193:8443/Service1/";
    private final Service1Api api;

    public CityServiceClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JaxbConverterFactory.create())
                .build();

        api = retrofit.create(Service1Api.class);
    }

    public City getCity(int id) {
        try {
            Response<City> response = api.getCityById(id).execute();
            switch (response.code()) {
                case 200:
                    City found = response.body();
                    if(found == null) {
                        throw new NotFoundException("Service1 returned an empty city");
                    }
                    return response.body();
                case 404:
                    throw new NotFoundException("Couldn't find city with id = " + id);
                default:
                    throw new ApiErrorException("Unknown response from Service1: status code " + response.code());
            }
        } catch (IOException e) {
            throw new NetworkErrorException(e.getMessage());
        }
    }

    public City updateCity(City city) {
        try {
            CityInput cityInput = CityMapper.toCityInput(city);
            Response<City> response = api.updateCity(city.getId(), cityInput).execute();
            switch (response.code()){
                case 200:
                    City updated = response.body();
                    if(updated == null) {
                        throw new NotFoundException("Service1 returned an empty city");
                    }
                    return response.body();
                case 400:
                    throw new InvalidRequestException("Provided an invalid request!");
                case 404:
                    throw new NotFoundException("Couldn't find city with id = " + city.getId());
                default:
                    throw new ApiErrorException("Unknown response from Service1: status code " + response.code());
            }
        } catch (IOException e) {
            throw new NetworkErrorException(e.getMessage());
        }
    }

    public List<City> getAllCities() {
        try {
            CitySearchRequest request = new CitySearchRequest();
            Pagination pagination = new Pagination(0, 1000);
            request.setPagination(pagination);
            Sort sort = new Sort("populationDensity", "ASC");
            request.setSort(sort);

            Response<CityPageResponse> response = api.searchCities(request).execute();
            switch (response.code()){
                case 200:
                    CityPageResponse cityPage = response.body();
                    if (cityPage == null) {
                        throw new ApiErrorException("Got empty cityPage");
                    }
                    if (cityPage.getCities() == null || cityPage.getCities().isEmpty()) {
                        throw new EmptyCitiesListException("Got empty cities list");
                    }
                    return cityPage.getCities();
                case 400:
                    throw new InvalidRequestException("Provided an invalid request!");
                default:
                    throw new ApiErrorException("Unknown response from Service1: status code " + response.code());
            }
        } catch (IOException e) {
            throw new NetworkErrorException(e.getMessage());
        }
    }
}
