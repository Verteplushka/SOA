package edu.itmo.soa.newservice2.rest_layer.controller;

import com.example.soap.client.*;
import edu.itmo.soa.newservice2.rest_layer.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;

@RestController
@RequestMapping(value = "/genocide", produces = MediaType.APPLICATION_XML_VALUE)
public class GenocideProxyController {

    private static final Logger log = LoggerFactory.getLogger(GenocideProxyController.class);

    private final WebServiceTemplate template;
    private final String soapUri;

    public GenocideProxyController(
            WebServiceTemplate template,
            @Value("${soap.endpoint.uri}") String soapUri
    ) {
        this.template = template;
        this.soapUri = soapUri;
    }

    @PostMapping("/count/{id1}/{id2}/{id3}")
    public ResponseEntity<?> countPopulation(
            @PathVariable int id1,
            @PathVariable int id2,
            @PathVariable int id3
    ) {
        CountPopulationRequest request = new CountPopulationRequest();
        request.setId1(id1);
        request.setId2(id2);
        request.setId3(id3);

        try {
            log.info("Calling SOAP countPopulation with ids: {}/{}/{}", id1, id2, id3);

            CountPopulationResponse response =
                    (CountPopulationResponse) template.marshalSendAndReceive(
                            soapUri,
                            request,
                            msg -> ((SoapMessage) msg).setSoapAction("")
                    );

            return ResponseEntity.ok(
                    new CountResultDto(response.getTotalPopulation())
            );

        } catch (Exception e) {
            log.error("SOAP countPopulation failed", e);
            return ResponseEntity.status(404)
                    .body(new ErrorDto("Один или несколько городов не найдены"));
        }
    }

    @PostMapping("/move-to-poorest/{id}")
    public ResponseEntity<?> moveToPoorest(@PathVariable int id) {

        MoveToPoorestRequest request = new MoveToPoorestRequest();
        request.setSourceCityId(id);

        try {
            log.info("Calling SOAP moveToPoorest with sourceCityId={}", id);

            MoveToPoorestResponse response =
                    (MoveToPoorestResponse) template.marshalSendAndReceive(
                            soapUri,
                            request,
                            msg -> ((SoapMessage) msg).setSoapAction("")
                    );

            MoveResultDto result = new MoveResultDto();
            result.setSourceCity(new CityWrapper(response.getSourceCity()));
            result.setTargetCity(new CityWrapper(response.getTargetCity()));

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("SOAP moveToPoorest failed", e);
            return ResponseEntity.status(404)
                    .body(new ErrorDto("Указанный город не найден"));
        }
    }
}
