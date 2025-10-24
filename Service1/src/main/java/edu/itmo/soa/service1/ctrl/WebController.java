package edu.itmo.soa.service1.ctrl;

import org.springframework.web.bind.annotation.*;

@RestController
public class WebController {
    @GetMapping("/get/{name}")
    public String hello(@PathVariable("name") String name) {
        return "Hello, " + name;
    }
}
