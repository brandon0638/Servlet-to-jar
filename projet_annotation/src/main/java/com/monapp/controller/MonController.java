package com.monapp.controller;

import com.monapp.annotation.MaPremiereAnnotation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonController {
    
    @GetMapping("/test")
    @MaPremiereAnnotation(valeur = "Hello", actif = true)
    public String test() {
        return "OK";
    }
}