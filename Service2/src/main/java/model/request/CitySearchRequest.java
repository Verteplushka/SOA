package model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "CitySearchRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CitySearchRequest {

    private Pagination pagination;
    private Sort sort;
    private Filter filter = new Filter();
}
