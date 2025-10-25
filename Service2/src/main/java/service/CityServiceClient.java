package service;

import jakarta.ws.rs.client.*;
import model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MediaType;

import javax.net.ssl.HostnameVerifier;
import java.util.List;

@ApplicationScoped
public class CityServiceClient {

    private final String BASE_URL = "http://localhost:8080/Service1/cities"; // URL первого сервиса (Spring Jetty)
    private final Client client;

    public CityServiceClient() {
        // Создаём билдер клиента
        ClientBuilder builder = ClientBuilder.newBuilder();

        // Устанавливаем проверку хоста, которая всегда возвращает true
        HostnameVerifier allowAllHosts = (hostname, session) -> true;
        builder.hostnameVerifier(allowAllHosts);

        // Строим клиент
        this.client = builder.build();
    }


    public City getCity(int id) {
        try {
            // Создаем цель запроса (URL сервиса с нужным id)
            WebTarget target = client.target(BASE_URL + "/" + id);

            // Выполняем GET-запрос и указываем, что хотим получить XML
            Invocation.Builder requestBuilder = target.request(MediaType.APPLICATION_XML);

            // Получаем объект City из ответа
            City city = requestBuilder.get(City.class);

            return city;
        } catch (Exception e) {
            // Если что-то пошло не так (например, сервис недоступен), возвращаем null
            return null;
        }
    }

    public List<City> getAllCities() {
        try {
            // Создаем объект запроса для поиска городов
            CitySearchRequest request = new CitySearchRequest();

            // Устанавливаем пагинацию: берем всех сразу (с 0 по 1000)
            Pagination pagination = new Pagination(0, 1000);
            request.setPagination(pagination);

            // Устанавливаем сортировку по плотности населения в порядке возрастания
            Sort sort = new Sort("populationDensity", "ASC");
            request.setSort(sort);

            // Создаем цель запроса к сервису
            WebTarget target = client.target(BASE_URL + "/search");

            // Формируем объект запроса с указанием, что передаем XML
            Invocation.Builder requestBuilder = target.request(MediaType.APPLICATION_XML);

            // Выполняем POST-запрос, передаем объект запроса и получаем ответ в виде cityPageResponse
            cityPageResponse response = requestBuilder.post(
                    Entity.entity(request, MediaType.APPLICATION_XML),
                    cityPageResponse.class
            );

            // Возвращаем список городов из ответа
            return response.getCities();

        } catch (Exception e) {
            // В случае ошибки выводим стек и возвращаем пустой список
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
            HumanInput gov = new HumanInput();
            gov.setAge(city.getGovernor().getAge());
            input.setGovernor(gov);
        }
        return input;
    }

    public void updateCity(City city) {
        // Преобразуем объект City в формат, который принимает сервис (CityInput)
        CityInput cityInput = toCityInput(city);
        // Создаем URL запроса к сервису для обновления конкретного города
        WebTarget target = client.target(BASE_URL + "/" + city.getId());
        // Создаем объект запроса, указывая, что будем работать с XML
        Invocation.Builder requestBuilder = target.request(MediaType.APPLICATION_XML);
        // Выполняем PUT-запрос, отправляя cityInput в формате XML
        requestBuilder.put(Entity.entity(cityInput, MediaType.APPLICATION_XML));
    }

}
