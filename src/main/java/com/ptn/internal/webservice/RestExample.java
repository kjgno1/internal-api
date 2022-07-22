package com.ptn.internal.webservice;



import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("")
public class RestExample {
    private static final Logger log = LogManager.getLogger(RestExample.class);
    @RequestMapping("/init")
    public ResponseEntity<String> init(){
        log.info("controller");
        log.info("controller");
        log.info("controller");
        log.info("controller");
        log.info("controller");
        return ResponseEntity.ok("OK");
    }
}
