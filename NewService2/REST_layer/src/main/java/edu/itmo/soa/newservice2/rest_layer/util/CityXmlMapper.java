package edu.itmo.soa.newservice2.rest_layer.util;

import com.example.soap.client.*;

import javax.xml.datatype.XMLGregorianCalendar;

public class CityXmlMapper {

    public static String toXml(City city) {
        if (city == null) {
            return "<city>null</city>";
        }

        StringBuilder sb = new StringBuilder("<city>");

        sb.append("<id>").append(city.getId()).append("</id>");
        sb.append("<name>").append(escapeXml(city.getName())).append("</name>");

        Coordinates coords = city.getCoordinates();
        if (coords != null) {
            sb.append("<coordinates>");
            sb.append("<id>").append(coords.getId()).append("</id>");
            sb.append("<x>").append(coords.getX()).append("</x>");
            sb.append("<y>").append(coords.getY()).append("</y>");
            sb.append("</coordinates>");
        } else {
            sb.append("<coordinates>null</coordinates>");
        }

        sb.append("<creationDate>").append(formatDate(city.getCreationDate())).append("</creationDate>");

        if (city.getArea() != null) {
            sb.append("<area>").append(city.getArea()).append("</area>");
        }

        sb.append("<population>").append(city.getPopulation()).append("</population>");

        if (city.getMetersAboveSeaLevel() != null) {
            sb.append("<metersAboveSeaLevel>").append(city.getMetersAboveSeaLevel()).append("</metersAboveSeaLevel>");
        }

        sb.append("<establishmentDate>").append(formatDate(city.getEstablishmentDate())).append("</establishmentDate>");

        if (city.getPopulationDensity() != null) {
            sb.append("<populationDensity>").append(city.getPopulationDensity()).append("</populationDensity>");
        }

        if (city.getGovernment() != null) {
            sb.append("<government>").append(city.getGovernment().value()).append("</government>");
        }

        Human governor = city.getGovernor();
        if (governor != null) {
            sb.append("<governor>");
            sb.append("<id>").append(governor.getId()).append("</id>");
            sb.append("<age>").append(governor.getAge()).append("</age>");
            sb.append("</governor>");
        }

        sb.append("</city>");
        return sb.toString();
    }

    private static String formatDate(XMLGregorianCalendar cal) {
        return cal != null ? cal.toXMLFormat() : "";
    }

    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
