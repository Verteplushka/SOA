package edu.itmo.soa.service1.dto.response;

import com.fasterxml.jackson.dataformat.xml.annotation.*;
import edu.itmo.soa.service1.entity.CityDto;

import java.io.Serializable;
import java.util.List;

@JacksonXmlRootElement(localName = "citiesResponse")
public class CitiesResponse implements Serializable {

    @JacksonXmlElementWrapper(localName = "cities")
    @JacksonXmlProperty(localName = "city")
    private List<CityDto> cities;

    public CitiesResponse() {}
    public CitiesResponse(List<CityDto> cities) {
        this.cities = cities;
    }

    public List<CityDto> getCities() {
        return cities;
    }
}

