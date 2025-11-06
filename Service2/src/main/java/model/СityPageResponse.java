package model;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@XmlRootElement(name = "cityPageResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class СityPageResponse {

    //@XmlElement(name = "cities")
    @XmlElementWrapper(name = "cities") // outer <cities>
    @XmlElement(name = "cities")        // inner <cities>
    private List<City> cities;

}
