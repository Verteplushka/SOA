package model;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@XmlRootElement(name = "cityPageResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CityPageResponse {
    @XmlElementWrapper(name = "cities")
    @XmlElement(name = "city")
    private List<City> cities;
}
