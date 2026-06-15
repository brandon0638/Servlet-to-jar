package com.monapp.controller; 
 
import com.monapp.annotation.MonLog; 
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.RestController; 
 
@RestController 
public class TestController { 
 
    @GetMapping("/hello") 
    @MonLog(valeur = "Test reussi") 
    public String hello() { 
        return "Hello World!"; 
    } 
} 
