package edu.itmo.soa.service1.dto.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import edu.itmo.soa.service1.entity.City;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "cityPageResponse")
public class CityPageResponse {

    private Pagination pagination;
    private List<City> cities;

    @Data
    public static class Pagination {
        private int currentPage;
        private int totalPages;
        private int pageSize;
    }
}

