package edu.itmo.soa.service1.dto.response;

import edu.itmo.soa.service1.entity.City;
import edu.itmo.soa.service1.entity.CityDto;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import com.fasterxml.jackson.dataformat.xml.annotation.*;

@JacksonXmlRootElement(localName = "citiesResponse")
public class CitiesResponse extends RepresentationModel<CitiesResponse> {

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

