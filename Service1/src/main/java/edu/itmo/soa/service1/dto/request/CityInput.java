package edu.itmo.soa.service1.dto.request;

import edu.itmo.soa.service1.entity.Government;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CityInput implements Serializable {
    @NotBlank
    private String name;

    @NotNull
    private CoordinatesInput coordinates;

    @NotNull
    @Positive
    private Integer area;

    @PositiveOrZero
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
    public static class CoordinatesInput implements Serializable {
        @NotNull
        private Double x;
        @NotNull
        private Double y;
    }

    @Data
    public static class HumanInput implements Serializable {
        @Positive
        private int age;
    }
}

