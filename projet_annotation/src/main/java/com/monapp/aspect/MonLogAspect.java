package com.monapp.aspect;

import com.monapp.annotation.MonLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MonLogAspect {
    
    @Before("@annotation(monLog)")
    public void avantMethode(JoinPoint joinPoint, MonLog monLog) {
        System.out.println(">>> Annotation détectée !");
        System.out.println(">>> Méthode: " + joinPoint.getSignature().getName());
        System.out.println(">>> Valeur: " + monLog.valeur());
    }
}