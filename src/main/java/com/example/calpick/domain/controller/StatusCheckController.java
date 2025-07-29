package com.example.calpick.domain.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Status Check")
public class StatusCheckController {
    @GetMapping("/")
    public ResponseEntity<Void> checkHealthStatus(){
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
