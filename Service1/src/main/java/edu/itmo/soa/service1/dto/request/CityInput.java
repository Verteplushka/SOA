package edu.itmo.soa.service1.dto.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import edu.itmo.soa.service1.entity.Government;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CityInput {

    @NotBlank
    private String name;

    @NotNull
    private CoordinatesInput coordinates;

    @NotNull
    @Positive
    private Integer area;

    @Positive
    private long population;

    private Integer metersAboveSeaLevel;

    private LocalDateTime establishmentDate;

    @Positive
    private Float populationDensity;

    @NotNull
    private Government government;

    @NotNull
    private HumanInput governor;

    @Data
    public static class CoordinatesInput {
        @NotNull
        private Double x;
        @NotNull
        private Double y;
    }

    @Data
    public static class HumanInput {
        @Positive
        private int age;
    }
}

