package edu.itmo.soa.service1.dto.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JacksonXmlRootElement(localName = "CitySearchRequest")
public class CitySearchRequest implements Serializable {

    private Pagination pagination;
    private Sort sort;
    private Filter filter;

    @Data
    public static class Pagination implements Serializable {
        private int page = 0;
        private int size = 20;
    }

    @Data
    public static class Sort implements Serializable {
        private String field = "id";
        private Direction direction = Direction.ASC;

        public enum Direction { ASC, DESC }
    }

    @Data
    public static class Filter implements Serializable {
        private String name;
        private CoordinatesFilter coordinates;
        private CreationDateRange creationDate;
        private Range<Integer> area;
        private Range<Long> population;
        private Range<Integer> metersAboveSeaLevel;
        private EstablishmentDateTimeRange establishmentDate;
        private Range<Float> populationDensity;
        private String government;
        private HumanFilter governor;
    }

    @Data
    public static class CreationDateRange implements Serializable {
        private LocalDate min;
        private LocalDate max;
    }

    @Data
    public static class EstablishmentDateTimeRange implements Serializable {
        private LocalDateTime min;
        private LocalDateTime max;
    }

    @Data
    public static class CoordinatesFilter implements Serializable {
        private Range<Double> x;
        private Range<Double> y;
    }

    @Data
    public static class HumanFilter implements Serializable {
        private Range<Integer> age;
    }

    @Data
    public static class Range<T extends Comparable<T>> implements Serializable {
        private T min;
        private T max;
    }

    @Data
    public static class DateRange<T extends Comparable<T>> implements Serializable {
        private T min;
        private T max;
    }
}

