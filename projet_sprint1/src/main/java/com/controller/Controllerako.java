package com.controller;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) //l'annotation existe pendant execution
@Target(ElementType.TYPE) //TYPE = classe , METHOD = methode, FIELD = variable
public @interface Controllerako{

}