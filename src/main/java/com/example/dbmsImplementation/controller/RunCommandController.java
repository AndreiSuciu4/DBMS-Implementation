package com.example.dbmsImplementation.controller;

import com.example.dbmsImplementation.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/command")
@CrossOrigin
public class RunCommandController {
    @Autowired
    private CatalogService catalogService;
    @PostMapping("/run-command")
    public ResponseEntity<?> getString(@RequestBody String command) {
        try {
            catalogService.runCommand(command);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);}
    }
}
