package edu.itmo.soa.service1.dto.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import edu.itmo.soa.service1.entity.CityDto;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.List;

@JacksonXmlRootElement(localName = "citiesResponse")
public class CitiesResponse extends RepresentationModel<CitiesResponse> implements Serializable {

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

