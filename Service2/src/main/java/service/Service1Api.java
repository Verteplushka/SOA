package service;

import model.City;
import model.CityInput;
import model.CityPageResponse;
import model.CitySearchRequest;
import retrofit2.Call;
import retrofit2.http.*;

public interface Service1Api {
    @GET("cities/{id}")
    Call<City> getCityById(@Path("id") int id);
    @PUT("cities/{id}")
    Call<City> updateCity(@Path("id") int id, @Body CityInput request);
    @POST("cities/search")
    Call<CityPageResponse> searchCities(@Body CitySearchRequest request);
}
