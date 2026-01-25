package edu.itmo.soa.newservice2.eurekaclient.model.request;

import edu.itmo.soa.newservice2.eurekaclient.model.Filter;
import edu.itmo.soa.newservice2.eurekaclient.model.Pagination;
import edu.itmo.soa.newservice2.eurekaclient.model.Sort;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CitySearchRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CitySearchRequest {

    private Pagination pagination;
    private Sort sort;
    private Filter filter = new Filter();

    public CitySearchRequest() {
    }

    public CitySearchRequest(Pagination pagination, Sort sort, Filter filter) {
        this.pagination = pagination;
        this.sort = sort;
        this.filter = filter;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
