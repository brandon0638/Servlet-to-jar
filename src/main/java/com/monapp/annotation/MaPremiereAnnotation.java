package com.monapp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)           // Sur les méthodes
@Retention(RetentionPolicy.RUNTIME)   // Visible à l'exécution
public @interface MaPremiereAnnotation {
    
    String valeur() default "";
    boolean actif() default true;
}