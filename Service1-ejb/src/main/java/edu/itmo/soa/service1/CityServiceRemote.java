package edu.itmo.soa.service1;

import edu.itmo.soa.service1.dto.request.CitySearchRequest;
import edu.itmo.soa.service1.dto.response.CitiesResponse;
import edu.itmo.soa.service1.dto.request.CityInput;
import edu.itmo.soa.service1.dto.response.CityPageResponse;
import edu.itmo.soa.service1.entity.City;
import jakarta.ejb.Remote;

@Remote
public interface CityServiceRemote {
    City findById(int id);
    City createCity(CityInput input);
    City updateCity(int id, CityInput in);
    void deleteById(int id);
    CityPageResponse searchCities(CitySearchRequest req);
    void deleteByMetersAboveSeaLevel(int meters);
    CitiesResponse findByNamePrefix(String prefix);
    CitiesResponse getCitiesByGovernorAge(int age);
}
