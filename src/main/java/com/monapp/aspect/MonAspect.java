package com.monapp.aspect;

import com.monapp.annotation.MaPremiereAnnotation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MonAspect {
    
    @Before("@annotation(monAnnotation)")
    public void avantMethode(JoinPoint joinPoint, MaPremiereAnnotation monAnnotation) {
        
        System.out.println("=== L'annotation est détectée ===");
        System.out.println("Méthode: " + joinPoint.getSignature().getName());
        System.out.println("Valeur: " + monAnnotation.valeur());
        System.out.println("Actif: " + monAnnotation.actif());
    }
}