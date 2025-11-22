package edu.itmo.soa.service1.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@JacksonXmlRootElement(localName = "city")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityDto extends RepresentationModel<CityDto> {
    private int id;
    private String name;
    private Coordinates coordinates;
    @JacksonXmlProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd'T'HH:mm:ss.SSSX")
    private ZonedDateTime creationDate;
    private Integer area;
    private long population;
    private Integer metersAboveSeaLevel;
    @JacksonXmlProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd'T'HH:mm:ss")
    private LocalDateTime establishmentDate;
    private Float populationDensity;
    private Government government;
    private Human governor;
}

