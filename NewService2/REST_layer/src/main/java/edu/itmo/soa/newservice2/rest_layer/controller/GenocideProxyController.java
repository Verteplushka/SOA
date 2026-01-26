package edu.itmo.soa.newservice2.rest_layer.controller;
import edu.itmo.soa.newservice2.rest_layer.util.CityXmlMapper;
import com.example.soap.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.soap.SoapMessage;



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
                            message -> ((SoapMessage) message).setSoapAction("")
                    );

            String xml = String.format(
                    "<result><totalPopulation>%d</totalPopulation></result>",
                    response.getTotalPopulation()
            );

            log.info("SOAP response received, totalPopulation: {}", response.getTotalPopulation());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml);

        } catch (Exception e) {
            log.error("Error calling SOAP countPopulation", e);
            return ResponseEntity.status(404)
                    .contentType(MediaType.APPLICATION_XML)
                    .body("<error>Один или несколько городов не найдены</error>");
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
                            message -> ((SoapMessage) message).setSoapAction("")
                    );

            StringBuilder xml = new StringBuilder("<result>");

            xml.append("<sourceCity>").append(CityXmlMapper.toXml(response.getSourceCity())).append("</sourceCity>");
            xml.append("<targetCity>").append(CityXmlMapper.toXml(response.getTargetCity())).append("</targetCity>");

            xml.append("</result>");

            log.info("SOAP moveToPoorest successful");
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml.toString());

        } catch (Exception e) {
            log.error("Error calling SOAP moveToPoorest", e);
            return ResponseEntity.status(404)
                    .contentType(MediaType.APPLICATION_XML)
                    .body("<error>Указанный город не найден</error>");
        }
    }

}