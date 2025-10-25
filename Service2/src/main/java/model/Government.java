package model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "government")
@XmlEnum
public enum Government {
    @XmlEnumValue("OLIGARCHY") OLIGARCHY,
    @XmlEnumValue("MONARCHY") KRITARCHY,
    @XmlEnumValue("DIARCHY") REPUBLIC;
}
