package edu.itmo.soa.newservice2.rest_layer.controller;

import com.example.soap.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.soap.SoapMessage;
import javax.xml.datatype.XMLGregorianCalendar;


@RestController
@RequestMapping("/genocide")
public class GenocideProxyController {

    private static final Logger log = LoggerFactory.getLogger(GenocideProxyController.class);

    private final WebServiceTemplate template;
    private final String soapUri;

    public GenocideProxyController(WebServiceTemplate template,
                                   @Value("${soap.endpoint.uri}") String soapUri) {
        this.template = template;
        this.soapUri = soapUri;
    }

    @PostMapping(value = "/count/{id1}/{id2}/{id3}",
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> countPopulation(
            @PathVariable int id1,
            @PathVariable int id2,
            @PathVariable int id3) {

        CountPopulationRequest request = new CountPopulationRequest();
        request.setId1(id1);
        request.setId2(id2);
        request.setId3(id3);

        try {
            log.info("Calling SOAP countPopulation with ids: {}/{}/{}", id1, id2, id3);

            CountPopulationResponse response = (CountPopulationResponse)
                    template.marshalSendAndReceive(
                            soapUri,
                            request,
                            message -> ((SoapMessage) message).setSoapAction("")  // как в твоём curl
                    );

            String xml = String.format(
                    "<result><totalPopulation>%d</totalPopulation></result>",
                    response.getTotalPopulation()
            );

            log.info("SOAP response received, totalPopulation: {}", response.getTotalPopulation());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml);

        } catch (Exception e) {
            log.error("Error calling SOAP countPopulation", e);
            return ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_XML)
                    .body("<error>SOAP call failed: " + e.getMessage() + "</error>");
        }
    }

    @PostMapping(value = "/move-to-poorest/{id}",
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> moveToPoorest(@PathVariable int id) {

        MoveToPoorestRequest request = new MoveToPoorestRequest();
        request.setSourceCityId(id);

        try {
            log.info("Calling SOAP moveToPoorest with sourceCityId: {}", id);

            MoveToPoorestResponse response = (MoveToPoorestResponse)
                    template.marshalSendAndReceive(
                            soapUri,
                            request,
                            message -> ((SoapMessage) message).setSoapAction("")  // как в твоём curl
                    );

            StringBuilder xml = new StringBuilder("<result>");

            xml.append("<sourceCity>").append(buildCityXml(response.getSourceCity())).append("</sourceCity>");
            xml.append("<targetCity>").append(buildCityXml(response.getTargetCity())).append("</targetCity>");

            xml.append("</result>");

            log.info("SOAP moveToPoorest successful");
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml.toString());

        } catch (Exception e) {
            log.error("Error calling SOAP moveToPoorest", e);
            return ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_XML)
                    .body("<error>SOAP call failed: " + e.getMessage() + "</error>");
        }
    }

    private String buildCityXml(City city) {
        if (city == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("<id>").append(city.getId()).append("</id>");
        sb.append("<name>").append(escapeXml(city.getName())).append("</name>");

        Coordinates coords = city.getCoordinates();
        System.out.println(city);
        System.out.println(coords);
        if (coords != null) {
            sb.append("<coordinates>");
            sb.append("<id>").append(coords.getId()).append("</id>");
            sb.append("<x>").append(coords.getX()).append("</x>");
            sb.append("<y>").append(coords.getY()).append("</y>");
            sb.append("</coordinates>");
        }else{
            sb.append("<coordinates>null</coordinates>");
        }

        sb.append("<creationDate>").append(formatDate(city.getCreationDate())).append("</creationDate>");

        Integer area = city.getArea();
        if (area != null) {
            sb.append("<area>").append(area).append("</area>");
        }

        sb.append("<population>").append(city.getPopulation()).append("</population>");

        Integer meters = city.getMetersAboveSeaLevel();
        if (meters != null) {
            sb.append("<metersAboveSeaLevel>").append(meters).append("</metersAboveSeaLevel>");
        }

        sb.append("<establishmentDate>").append(formatDate(city.getEstablishmentDate())).append("</establishmentDate>");

        Float density = city.getPopulationDensity();
        if (density != null) {
            sb.append("<populationDensity>").append(density).append("</populationDensity>");
        }

        Government gov = city.getGovernment();
        if (gov != null) {
            sb.append("<government>").append(gov.value()).append("</government>");
        }

        Human governor = city.getGovernor();
        if (governor != null) {
            sb.append("<governor>");
            sb.append("<id>").append(governor.getId()).append("</id>");
            sb.append("<age>").append(governor.getAge()).append("</age>");
            sb.append("</governor>");
        }

        return sb.toString();
    }

    private String formatDate(XMLGregorianCalendar cal) {
        if (cal == null) {
            return "";
        }
        return cal.toXMLFormat();
    }

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}