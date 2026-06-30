package com.test.controller;

import com.annotation.Controllerako;
import com.annotation.UrlMapping;


@Controllerako
public class EmpController{

    @UrlMapping("/emp/list")
    public void list(){

    }

    @UrlMapping(value = "/emp/new", method = "POST")
    public void create(){

    }

    @UrlMapping("/emp/delete")
    public void delete(){

    }
    @UrlMapping("/")
    public String accueil() {
        return "bonjour";
        // Cette méthode est vide
    }
}
