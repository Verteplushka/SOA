package edu.itmo.soa.newservice2.eurekaclient.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Pagination {
    private int page;
    private int size;

    public Pagination(int page, int size) {
        this.page = page;
        this.size = size;
    }
}
