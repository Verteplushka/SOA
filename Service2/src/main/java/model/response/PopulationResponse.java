package model.response;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlRootElement(name = "population")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class PopulationResponse {
    private long totalPopulation;
}
