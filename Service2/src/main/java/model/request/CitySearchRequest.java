package model.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import model.Filter;
import model.Pagination;
import model.Sort;

@XmlRootElement(name = "CitySearchRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CitySearchRequest {
    private Pagination pagination;
    private Sort sort;
    private Filter filter = new Filter();
}
