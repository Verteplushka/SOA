package rest;

import exception.ApiErrorException;
import model.*;
import model.response.ErrorResponse;
import model.response.PopulationResponse;
import model.response.RelocationResponse;
import service.CityServiceClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Comparator;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class GenocideResource {

    @Inject
    private CityServiceClient cityServiceClient;

    @GET
    @Path("/city/{id}")
    public Response getCityById(@PathParam("id") int id) {
        try {
            City city = cityServiceClient.getCity(id);

            if (city == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("NOT_FOUND", "Город с id " + id + " не найден"))
                        .build();
            }
            return Response.ok(city).build();
        } catch (ApiErrorException e) {
            return Response.serverError()
                    .entity(new ErrorResponse("SERVICE1_API_ERROR", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/count/{id1}/{id2}/{id3}")
    public Response countPopulation(
            @PathParam("id1") int id1,
            @PathParam("id2") int id2,
            @PathParam("id3") int id3) {

        try {
            City c1 = cityServiceClient.getCity(id1);
            City c2 = cityServiceClient.getCity(id2);
            City c3 = cityServiceClient.getCity(id3);

            if (c1 == null || c2 == null || c3 == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("NOT_FOUND", "Один или несколько городов не найдены"))
                        .build();
            }

            long totalPopulation = c1.getPopulation() + c2.getPopulation() + c3.getPopulation();
            PopulationResponse result = new PopulationResponse();
            result.setTotalPopulation(totalPopulation);
            return Response.ok(result).build();
        } catch (ApiErrorException e) {
            return Response.serverError()
                    .entity(new ErrorResponse("SERVICE1_API_ERROR", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/move-to-poorest/{id}")
    public Response moveToPoorest(@PathParam("id") int sourceId) {
        try {
            List<City> cities = cityServiceClient.getAllCities();

            if (cities.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("NOT_FOUND", "Нет доступных городов"))
                        .build();
            }

            City source = cityServiceClient.getCity(sourceId);
            if (source == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("NOT_FOUND", "Исходный город не найден"))
                        .build();
            }

            City poorest = cities.stream()
                    .filter(c -> c.getId() != source.getId())
                    .min(Comparator.comparingDouble(City::getPopulationDensity))
                    .orElse(null);

            if (poorest == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("INVALID_OPERATION", "Нет подходящего города для переселения"))
                        .build();
            }
            if (source.getId() == poorest.getId()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("INVALID_OPERATION", "Самый бедный город и город для переселения совпадают"))
                        .build();
            }

            poorest.setPopulation(poorest.getPopulation() + source.getPopulation());
            source.setPopulation(0);

            cityServiceClient.updateCity(poorest);
            cityServiceClient.updateCity(source);

            RelocationResponse response = new RelocationResponse();
            response.setSourceCity(source);
            response.setTargetCity(poorest);

            return Response.ok(response).build();

        } catch (ApiErrorException e) {
            return Response.serverError()
                    .entity(new ErrorResponse("SERVICE1_API_ERROR", e.getMessage()))
                    .build();
        }
    }
}
