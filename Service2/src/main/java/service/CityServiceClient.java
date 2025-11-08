package service;

import exception.ApiErrorException;
import jakarta.enterprise.context.ApplicationScoped;
import model.*;
import retrofit2.converter.jaxb.JaxbConverterFactory;
import retrofit2.Retrofit;
import okhttp3.OkHttpClient;
import util.CityMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class CityServiceClient {

    private final String BASE_URL = "https://158.160.204.193:8443/Service1/";
    private final Service1Api api;

    public CityServiceClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(JaxbConverterFactory.create())
                .build();

        api = retrofit.create(Service1Api.class);
    }

    public City getCity(int id) {
        try {
            return api.getCityById(id).execute().body();
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }
    }

    public City updateCity(City city) {
        try {
            CityInput cityInput = CityMapper.toCityInput(city);
            return api.updateCity(city.getId(), cityInput).execute().body();
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }
    }

    public List<City> getAllCities() {
        try {
            CitySearchRequest request = new CitySearchRequest();
            Pagination pagination = new Pagination(0, 1000); request.setPagination(pagination);
            Sort sort = new Sort("populationDensity", "ASC"); request.setSort(sort);

            CityPageResponse response = api.searchCities(request).execute().body();
            if (response == null || response.getCities() == null) {
                throw new ApiErrorException("No cities found");
            }
            return response.getCities();
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }
    }
}
