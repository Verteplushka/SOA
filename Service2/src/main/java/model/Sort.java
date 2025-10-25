package model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Sort {

    private String field;
    private String direction;


    public Sort(String field, String direction) {
        this.field = field;
        this.direction = direction;
    }

}
