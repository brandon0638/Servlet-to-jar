package com.test;

import com.controller.*;

public class Main {

    public static void main(String[] args) {

        HomeController c = new HomeController();
        c.hello();
        if (HomeController.class.isAnnotationPresent(Controllerako.class)) {

            System.out.println("Controller détecté !");
        }
    }
}