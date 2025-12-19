package edu.itmo.soa.newservice2.eurekaclient.model.request;

import edu.itmo.soa.newservice2.eurekaclient.model.Filter;
import edu.itmo.soa.newservice2.eurekaclient.model.Pagination;
import edu.itmo.soa.newservice2.eurekaclient.model.Sort;
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
