package model;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@XmlRootElement(name = "CityInput")
@XmlAccessorType(XmlAccessType.FIELD)
public class CityInput {
    private String name;
    private CoordinatesInput coordinates;
    private Integer area;
    private long population;
    private Integer metersAboveSeaLevel;
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime establishmentDate;
    private Float populationDensity;
    private Government government;
    private GovernorInput governor;
}
